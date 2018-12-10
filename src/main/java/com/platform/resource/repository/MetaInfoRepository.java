package com.platform.resource.repository;

import com.platform.resource.repository.entity.MetaInfo;
import com.platform.resource.repository.entity.MetaType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MetaInfoRepository extends JpaRepository<MetaInfo, Long> {

	List<MetaInfo> findAllByDeletedFalse();

	List<MetaInfo> findByMetaTypeAndDeletedFalse(MetaType metaType);
}
