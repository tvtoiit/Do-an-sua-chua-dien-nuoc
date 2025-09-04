INSERT INTO dbo.users (full_name, email, phone_number, address, password_hash, role, created_at, delete_flg)
VALUES
(N'Nguyễn Văn A', 'a@example.com', '0901234567', N'Hà Nội', 'hashed_pwd_123', 'Technician', GETDATE(), 0),
(N'Trần Thị B', 'b@example.com', '0912345678', N'Hồ Chí Minh', 'hashed_pwd_456', 'Customer', GETDATE(), 0),
(N'Lê Văn C', 'c@example.com', '0923456789', N'Đà Nẵng', 'hashed_pwd_789', 'Customer', GETDATE(), 0);


update users set ROLE = 'Admin' where email = 'tvtoiit@gmail.com';

select * from users

INSERT INTO dbo.technicians (user_id, experience_years, qualification, health_certificate, created_at, delete_flg)
VALUES
(1, 5, N'Kỹ sư điện lạnh', N'HC12345', GETDATE(), 0),
(3, 7, N'Chuyên gia sửa máy giặt', N'HC67890', GETDATE(), 0);

select * from technicians

update technicians set user_id = '5' where user_id = '3'

INSERT INTO dbo.reviews (customer_id, technician_id, service_id, rating, comment, created_at, delete_flg)
  VALUES
  (1, 5, 1, 5, N'Rất hài lòng, kỹ thuật viên nhanh và chuyên nghiệp!', GETDATE(), 0),
  (2, 6, 2, 4, N'Sửa ổn nhưng đến muộn 15 phút', GETDATE(), 0);



INSERT INTO dbo.contacts (name, email, phone, content, created_at, delete_flg)
  VALUES
  (N'Nguyễn Minh',  'minh@gmail.com', '0987654321', N'Tôi muốn đặt lịch sửa điều hòa', GETDATE(), 0),
  (N'Lê Hạnh',      'hanh@yahoo.com', '0977123456', N'Tôi cần tư vấn bảo hành máy giặt', GETDATE(), 0);

  INSERT INTO dbo.appliances (appliance_name, brand, model, technician_id, image_url, created_at, delete_flg)
VALUES
(N'Tủ lạnh',  N'Panasonic', N'NR-BX410', 5, 'https://example.com/fridge.jpg', GETDATE(), 0),
(N'Máy giặt', N'Samsung',   N'WA90J',    6, 'https://example.com/washer.jpg', GETDATE(), 0),
(N'Điều hòa', N'Daikin',    N'FTKC25',   5, 'https://example.com/aircon.jpg', GETDATE(), 0);

select * from appliances

  INSERT INTO dbo.repair_services (appliance_id, service_name, price, description, image_url, created_at, delete_flg)
VALUES
(2, N'Sửa tủ lạnh không lạnh', 500000, N'Khắc phục tình trạng tủ lạnh không làm mát', 'https://example.com/fridge_service.jpg', GETDATE(), 0),
(4, N'Sửa máy giặt không vắt', 400000, N'Sửa lỗi máy giặt không quay/không vắt', 'https://example.com/washer_service.jpg', GETDATE(), 0),
(3, N'Bảo dưỡng điều hòa',     300000, N'Vệ sinh, kiểm tra gas và bảo dưỡng định kỳ', 'https://example.com/aircon_service.jpg', GETDATE(), 0);


INSERT INTO dbo.repair_services (appliance_id, service_name, price, description, image_url, created_at, delete_flg)
VALUES
(2, N'Sửa tủ lạnh không lạnh', 500000, N'Khắc phục tình trạng tủ lạnh không làm mát', 'https://example.com/fridge_service.jpg', GETDATE(), 0),
(2, N'Sửa máy giặt không vắt', 400000, N'Sửa lỗi máy giặt không quay/không vắt', 'https://example.com/washer_service.jpg', GETDATE(), 0),
(3, N'Bảo dưỡng điều hòa',     300000, N'Vệ sinh, kiểm tra gas và bảo dưỡng định kỳ', 'https://example.com/aircon_service.jpg', GETDATE(), 0);

select * from repair_services

INSERT INTO dbo.repair_requests
(customer_id, technician_id, service_id, appliance_id, appointment_date, status, notes, reject_reason,
 created_at, update_at, delete_at, delete_flg, start_time, duration_minutes, extensions, progress)
VALUES
(1, 1, 3, 2, DATEADD(DAY, 1, GETDATE()), N'Pending', N'Khách hẹn sửa tủ lạnh', NULL,
 GETDATE(), NULL, NULL, 0, GETDATE(), 60, 0, 0),

(2, 3, 4, 3, DATEADD(DAY, 2, GETDATE()), N'Accepted', NULL, NULL,
 GETDATE(), NULL, NULL, 0, GETDATE(), 90, 0, 10),

(3, 1, 5, 4, DATEADD(DAY, -1, GETDATE()), N'Completed', N'Sửa điều hòa xong', NULL,
 GETDATE(), GETDATE(), NULL, 0, GETDATE(), 120, 1, 100);


INSERT INTO dbo.articles
(author_id, title, description, content, status, reason,
 created_at, update_at, delete_at, delete_flg)
VALUES
(1, 
 N'Mẹo bảo dưỡng điều hòa mùa hè', 
 N'Hướng dẫn vệ sinh, bảo dưỡng điều hòa để tiết kiệm điện.', 
 N'Nội dung chi tiết về cách vệ sinh lưới lọc, kiểm tra gas, bảo trì định kỳ...', 
 N'Approved', 
 NULL, 
 GETDATE(), 
 NULL, 
 NULL, 
 0),

(2, 
 N'Cách sử dụng máy giặt tiết kiệm nước', 
 N'Những thói quen giúp giảm lượng nước tiêu thụ.', 
 N'Nội dung gồm phân loại quần áo, chọn chế độ phù hợp, vệ sinh lồng giặt...', 
 N'Pending', 
 NULL, 
 GETDATE(), 
 NULL, 
 NULL, 
 0),

(3, 
 N'Nguyên nhân tủ lạnh không làm lạnh', 
 N'Bài viết chia sẻ các lỗi thường gặp và cách khắc phục.', 
 N'Nội dung gồm kiểm tra nguồn điện, quạt gió, block nén, vệ sinh dàn ngưng...', 
 N'Rejected', 
 N'Nội dung còn sơ sài, cần bổ sung hình ảnh minh họa.', 
 GETDATE(), 
 NULL, 
 NULL, 
 0);





 select * from technicians

 INSERT INTO dbo.reviews (customer_id, technician_id, service_id, rating, comment, created_at, delete_flg)
VALUES
(2, 5, 3, 5, N'Rất hài lòng, kỹ thuật viên nhanh và chuyên nghiệp!', GETDATE(), 0), -- Trần Thị B review A
(3, 6, 5, 4, N'Sửa ổn nhưng đến muộn 15 phút', GETDATE(), 0); -- Lê Văn C review C

