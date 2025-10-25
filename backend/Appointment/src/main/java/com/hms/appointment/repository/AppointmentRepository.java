package com.hms.appointment.repository;

import com.hms.appointment.dto.AppointmentDetails;
import com.hms.appointment.dto.MonthlyVisitDTO;
import com.hms.appointment.dto.ReasonCountDTO;
import com.hms.appointment.entity.Appointment;
import com.hms.appointment.exception.HmsException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends CrudRepository<Appointment,Long> {

    @Query("select new com.hms.appointment.dto.AppointmentDetails(a.id,a.patientId,null,null,null,a.doctorId,null,a.appointmentTime,a.status,a.reason,a.notes)"
    +"FROM Appointment  a  WHERE a.patientId = ?1")
    List<AppointmentDetails> findAllByPatientId(Long patientId);

    @Query("select new com.hms.appointment.dto.AppointmentDetails(a.id,a.patientId,null,null,null,a.doctorId,null,a.appointmentTime,a.status,a.reason,a.notes)"
            +"FROM Appointment  a  WHERE a.doctorId = ?1")
    List<AppointmentDetails> findAllByDoctorId(Long doctorId);

  @Query("Select new com.hms.appointment.dto.MonthlyVisitDTO( cast( FUNCTION('MONTHNAME',a.appointmentTime)as string ),count(a)) FROM Appointment a where a.patientId = ?1 AND YEAR(a.appointmentTime)=YEAR(CURRENT_DATE) GROUP BY FUNCTION('MONTH',a.appointmentTime),cast( FUNCTION('MONTHNAME',a.appointmentTime)as string )ORDER BY FUNCTION('MONTH',a.appointmentTime)")
    List<MonthlyVisitDTO> countCurrentYearVisitsByPatient(Long patientId);

    @Query("Select new com.hms.appointment.dto.MonthlyVisitDTO( cast( FUNCTION('MONTHNAME',a.appointmentTime)as string ),count(a)) FROM Appointment a where a.doctorId = ?1 AND YEAR(a.appointmentTime)=YEAR(CURRENT_DATE) GROUP BY FUNCTION('MONTH',a.appointmentTime),cast( FUNCTION('MONTHNAME',a.appointmentTime)as string )ORDER BY FUNCTION('MONTH',a.appointmentTime)")
    List<MonthlyVisitDTO> countCurrentYearVisitsByDoctor(Long doctorId);

    @Query("Select new com.hms.appointment.dto.MonthlyVisitDTO( cast( FUNCTION('MONTHNAME',a.appointmentTime)as string ),count(a)) FROM Appointment a where  YEAR(a.appointmentTime)=YEAR(CURRENT_DATE) GROUP BY FUNCTION('MONTH',a.appointmentTime),cast( FUNCTION('MONTHNAME',a.appointmentTime)as string )ORDER BY FUNCTION('MONTH',a.appointmentTime)")
    List<MonthlyVisitDTO> countCurrentYearVisits();

    @Query("Select new com.hms.appointment.dto.ReasonCountDTO(a.reason,count(a)) FROM Appointment a where a.patientId = ?1 GROUP BY a.reason")
  List<ReasonCountDTO> countReasonsByPatientId(Long patientId)throws HmsException  ;


    @Query("Select new com.hms.appointment.dto.ReasonCountDTO(a.reason,count(a)) FROM Appointment a where a.doctorId = ?1 GROUP BY a.reason")
    List<ReasonCountDTO> countReasonsByDoctorId(Long doctorId)throws HmsException  ;


    @Query("Select new com.hms.appointment.dto.ReasonCountDTO(a.reason,count(a)) FROM Appointment a GROUP BY a.reason")
    List<ReasonCountDTO> countReasons()throws HmsException  ;


    List<Appointment> findByAppointmentTimeBetween(LocalDateTime startOfDate,LocalDateTime endOfDate);
}