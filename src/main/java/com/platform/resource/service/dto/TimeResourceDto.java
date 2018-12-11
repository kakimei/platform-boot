package com.platform.resource.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeResourceDto implements Serializable {

	private Map<LocalDate, List<TimeDTO>> validDateMapDayForTEAM = new HashMap<>();

	private Map<LocalDate, List<TimeDTO>> validDateMapDayForSINGLE = new HashMap<>();

	private Map<LocalDate, List<TimeDTO>> validDateMapWeekForTEAM = new HashMap<>();

	private Map<LocalDate, List<TimeDTO>> validDateMapWeekForSINGLE = new HashMap<>();

	public static class TimeDTO implements Serializable{
		private Integer beginHour;
		private Integer beginMinute;
		private Integer endHour;
		private Integer endMinute;
		private Integer times;

		public TimeDTO(Integer beginHour, Integer beginMinute, Integer endHour, Integer endMinute, Integer times) {
			this.beginHour = beginHour;
			this.beginMinute = beginMinute;
			this.endHour = endHour;
			this.endMinute = endMinute;
			this.times = times;
		}

		public Integer getBeginHour() {
			return beginHour;
		}

		public void setBeginHour(Integer beginHour) {
			this.beginHour = beginHour;
		}

		public Integer getBeginMinute() {
			return beginMinute;
		}

		public void setBeginMinute(Integer beginMinute) {
			this.beginMinute = beginMinute;
		}

		public Integer getEndHour() {
			return endHour;
		}

		public void setEndHour(Integer endHour) {
			this.endHour = endHour;
		}

		public Integer getEndMinute() {
			return endMinute;
		}

		public void setEndMinute(Integer endMinute) {
			this.endMinute = endMinute;
		}

		public Integer getTimes() {
			return times;
		}

		public void setTimes(Integer times) {
			this.times = times;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			TimeDTO timeDTO = (TimeDTO) o;
			return Objects.equals(beginHour, timeDTO.beginHour) &&
				Objects.equals(beginMinute, timeDTO.beginMinute) &&
				Objects.equals(endHour, timeDTO.endHour) &&
				Objects.equals(endMinute, timeDTO.endMinute) &&
				Objects.equals(times, timeDTO.times);
		}

		@Override
		public int hashCode() {

			return Objects.hash(beginHour, beginMinute, endHour, endMinute, times);
		}
	}
}
