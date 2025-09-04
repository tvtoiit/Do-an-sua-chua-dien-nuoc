package com.example.CuoiKi.dto;

import java.time.OffsetDateTime;

public class RepairRequestTimingDTO {
	private OffsetDateTime  startTime;
	private Integer durationMinutes;
	private Integer extensions;
	private Integer progress;

	public OffsetDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(OffsetDateTime startTime) {
		this.startTime = startTime;
	}

	public Integer getDurationMinutes() {
		return durationMinutes;
	}

	public void setDurationMinutes(Integer durationMinutes) {
		this.durationMinutes = durationMinutes;
	}

	public Integer getExtensions() {
		return extensions;
	}

	public void setExtensions(Integer extensions) {
		this.extensions = extensions;
	}

	public Integer getProgress() {
		return progress;
	}

	public void setProgress(Integer progress) {
		this.progress = progress;
	}
}
