package com.maan.whatsapp.repository.whatsapp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.whatsapp.entity.master.PreinspectionImageDetail;

@Repository
public interface PreInspectionDataImageRepo extends JpaRepository<PreinspectionImageDetail, Long>{

}
