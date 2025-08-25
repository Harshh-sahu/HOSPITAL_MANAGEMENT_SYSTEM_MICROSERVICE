package com.hms.appointment.repository;

import com.hms.appointment.dto.AppointmentDetails;
import com.hms.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends CrudRepository<Appointment,Long> {

    @Query("select new com.hms.appointment.dto.AppointmentDetails(a.id,a.patientId,null,null,null,a.doctorId,null,a.appointmentTime,a.status,a.reason,a.notes)"
    +"FROM Appointment  a  WHERE a.patientId = ?1")
    List<AppointmentDetails> findAllByPatientId(Long patientId);

    @Query("select new com.hms.appointment.dto.AppointmentDetails(a.id,a.patientId,null,null,null,a.doctorId,null,a.appointmentTime,a.status,a.reason,a.notes)"
            +"FROM Appointment  a  WHERE a.doctorId = ?1")
    List<AppointmentDetails> findAllByDoctorId(Long doctorId);

}
