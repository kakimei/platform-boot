package com.platform.sign.service;

import com.platform.sign.repository.SignReservationInfoRepository;
import com.platform.sign.repository.entity.SignReservationInfo;
import com.platform.sign.service.dto.SignReservationInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public class SignReservationInfoServiceImpl implements SignReservationInfoService {

	@Autowired
	private SignReservationInfoRepository signReservationInfoRepository;

	@Autowired
	private SignReservationInfoDtoTransferBuilder signReservationInfoDtoTransferBuilder;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(SignReservationInfoDTO signReservationInfoDTO) {
		if (signReservationInfoDTO == null || signReservationInfoDTO.getReservationInfoId() == null || StringUtils.isBlank(
			signReservationInfoDTO.getUserName())) {
			log.warn("sign in failed, the sign reservation info is null.");
			return;
		}
		List<SignReservationInfo> signReservationInfoList = signReservationInfoRepository.findByUserNameAndReservationInfoIdAndSignInTrue(
			signReservationInfoDTO.getUserName(),
			signReservationInfoDTO.getReservationInfoId());
		if(CollectionUtils.isEmpty(signReservationInfoList)){
			SignReservationInfo signReservationInfo = signReservationInfoDtoTransferBuilder.toEntity(signReservationInfoDTO);
			signReservationInfo.setSignIn(true);
			signReservationInfoRepository.save(signReservationInfo);
		}
	}

	@Override
	public Boolean hasSignedByReservationInfoIdAndUserName(Long reservationInfoId, String userName) {
		List<SignReservationInfo> signReservationInfoList = signReservationInfoRepository.findByUserNameAndReservationInfoIdAndSignInTrue(
			userName, reservationInfoId);
		return !CollectionUtils.isEmpty(signReservationInfoList);
	}

	@Override
	public List<SignReservationInfoDTO> getSignedListByUserName(String userName) {
		List<SignReservationInfoDTO> result = new ArrayList<>();
		List<SignReservationInfo> signReservationInfoList = signReservationInfoRepository.findByUserNameAndSignInTrue(userName);
		if(CollectionUtils.isEmpty(signReservationInfoList)){
			return result;
		}
		signReservationInfoList.forEach(signReservationInfo -> result.add(signReservationInfoDtoTransferBuilder.toDTO(signReservationInfo)));
		return result;
	}
}
