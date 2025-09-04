package com.example.CuoiKi.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.CuoiKi.model.RepairRequest;
import com.example.CuoiKi.model.RepairService;
import com.example.CuoiKi.model.Technician;
import com.example.CuoiKi.repository.RepairRequestRepository;
import com.example.CuoiKi.repository.RepairServiceRepository;
import com.example.CuoiKi.repository.TechnicianRepository;
import com.example.CuoiKi.repository.UserRepository;

@Service
public class RepairRequestService {

	private final RepairRequestRepository repairRequestRepository;
	private final UserRepository userRepository;
	private final EmailService emailService;
	private final RepairServiceRepository repairServiceRepository;
	private final TechnicianRepository technicianRepository;

	public RepairRequestService(RepairRequestRepository repairRequestRepository, UserRepository userRepository,
			EmailService emailService, RepairServiceRepository repairServiceRepository,
			TechnicianRepository technicianRepository) {
		this.repairRequestRepository = repairRequestRepository;
		this.userRepository = userRepository;
		this.emailService = emailService;
		this.repairServiceRepository = repairServiceRepository;
		this.technicianRepository = technicianRepository;
	}

	public RepairRequest createRepairRequest(RepairRequest request) {
		LocalDateTime now = LocalDateTime.now();

		if (request.getAppointmentDate() == null || request.getAppointmentDate().isBefore(now)) {
			throw new IllegalArgumentException("Ngày hẹn sửa chữa phải là thời gian trong tương lai.");
		}

		// Kiểm tra và thiết lập lại customer
		userRepository.findById(request.getCustomer().getUserId()).ifPresentOrElse(request::setCustomer, () -> {
			throw new IllegalArgumentException("Khách hàng không tồn tại.");
		});

		// Kiểm tra service
		RepairService service = repairServiceRepository.findById(request.getService().getServiceId())
				.orElseThrow(() -> new IllegalArgumentException("Dịch vụ không tồn tại."));
		request.setService(service);

		// (Tùy chọn) kiểm tra thiết bị
		if (request.getAppliance() == null) {
			throw new IllegalArgumentException("Thiết bị không được để trống.");
		}

		request.setStatus("Pending");
		request.setCreatedAt(now);
		boolean exists = repairRequestRepository.existsByCustomerIdAndServiceIdAndApplianceIdAndStatus(
				request.getCustomer().getUserId(), request.getService().getServiceId(),
				request.getAppliance().getApplianceId(), "Pending");

		if (exists) {
			throw new IllegalArgumentException("Bạn đã có một lịch hẹn đang chờ xác nhận cho dịch vụ và thiết bị này.");
		}

		RepairRequest savedRequest = repairRequestRepository.save(request);

		// Gửi email xác nhận
		userRepository.findById(request.getCustomer().getUserId()).ifPresent(user -> {
			// Lấy ngày giờ từ request (đã là LocalDateTime)
			LocalDateTime appointmentDate = request.getAppointmentDate();

			// Định dạng lại ngày giờ theo định dạng dd-MM-yyyy HH:mm:ss
			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			String formattedDate = appointmentDate.format(outputFormatter);
			String subject = "Xác nhận đặt lịch sửa chữa";
			String content = "Xin chào " + user.getFullName() + ",\n\n" + "Bạn đã đặt lịch sửa chữa thành công.\n"
					+ "Dịch vụ: " + service.getServiceName() + "\n" + "Ngày hẹn: " + formattedDate + "\n"
					+ "Trạng thái: Chờ xác nhận\n\n" + "Chúng tôi sẽ sớm liên hệ với bạn để xác nhận lịch hẹn.\n\n"
					+ "Trân trọng,\nDịch vụ sửa chữa";
			emailService.sendEmail(user.getEmail(), subject, content);
		});

		return savedRequest;
	}

	public List<RepairRequest> getCustomerRequests(Integer customerId) {
		return repairRequestRepository.findByCustomer_UserId(customerId);
	}

	public Optional<RepairRequest> getRequestById(Integer requestId) {
		return repairRequestRepository.findById(requestId);
	}

	public RepairRequest updateRequestStatus(Integer requestId, String status, String rejectReason, Integer userId) {
		Optional<RepairRequest> optional = repairRequestRepository.findById(requestId);
		if (optional.isEmpty())
			return null;

		RepairRequest request = optional.get();
		request.setStatus(status);
		request.setRejectReason(rejectReason);

		if (userId != null) {
			Optional<Technician> technicianOpt = technicianRepository.findByUser_UserId(userId);
			if (technicianOpt.isPresent()) {
				request.setTechnicianId(technicianOpt.get().getTechnicianId()); // dùng Integer như bạn đang làm
			} else {
				System.out.println("Không tìm thấy technician từ userId: " + userId);
			}
		}

		return repairRequestRepository.save(request);
	}


	public RepairRequest cancelRepairRequest(Integer requestId, Integer customerId) {
		Optional<RepairRequest> requestOpt = repairRequestRepository.findById(requestId);

		if (requestOpt.isEmpty()) {
			throw new IllegalArgumentException("Không tìm thấy lịch hẹn.");
		}

		RepairRequest request = requestOpt.get();

		if (!request.getCustomer().getUserId().equals(customerId)) {
			throw new IllegalArgumentException("Bạn không có quyền hủy lịch này.");
		}

		if (!"Pending".equals(request.getStatus())) {
			throw new IllegalArgumentException("Không thể hủy lịch vì đã được xác nhận hoặc hoàn thành.");
		}

		request.setStatus("Cancel");
		RepairRequest updatedRequest = repairRequestRepository.save(request);

		userRepository.findById(customerId).ifPresent(user -> {
			String subject = "Hủy lịch hẹn thành công";
			String content = "Xin chào " + user.getFullName() + ",\n\n"
					+ "Lịch hẹn sửa chữa của bạn đã được hủy thành công.\n" + "Mã yêu cầu: " + request.getRequestId()
					+ "\n" + "Ngày hẹn: " + request.getAppointmentDate() + "\n" + "Trạng thái: Đã hủy\n\n"
					+ "Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi.\n\n" + "Trân trọng,\nDịch vụ sửa chữa";
			emailService.sendEmail(user.getEmail(), subject, content);
		});

		return updatedRequest;
	}

	public List<RepairRequest> getCompletedRequestsByCustomer(Integer customerId) {
		return repairRequestRepository.findByCustomer_UserIdAndStatus(customerId, "Completed");
	}

}
