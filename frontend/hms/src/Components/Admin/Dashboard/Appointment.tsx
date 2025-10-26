
import {
  appointments,

} from "../../../Data/DashboardData";
import { ScrollArea } from "@mantine/core";
import { useEffect, useState } from "react";
import { getTodaysAppointments } from "../../../Service/AppointmentService";
import { extractTimein12HourFormat } from "../../../Utility/DateUtility";

const Appointment = () => {

  const[tdAppointment,setTdAppointment]=useState<any[]>(appointments);  

  useEffect(()=>{
  
    getTodaysAppointments().then((res)=>{
      setTdAppointment(res);
    }).catch((err)=>{
      console.error("Error fetching today's appointments:", err);
    });
    

  },[]);
  const card = (app: any) => {
    return (
      <div className="p-3 m-3 border rounded-xl justify-between border-l-4
       border-violet-500 shadow-md flex bg-violet-100" key={app.id}>
        <div>
          <div className="font-semibold text-sm">{app.patientName}</div>
          <div className="text-sm text-gray-500">Dr. {app.doctorName}</div>
        </div>
        <div className="text-right">
          <div className="text-xs text-gray-500">{extractTimein12HourFormat(app.appointmentTime)}</div>
          <div className="text-xs text-gray-500">{app.reason}</div>
        </div>
      </div>
    );
  };

  return (
    <div className="p-3 border rounded-xl bg-violet-50 shadow-xl flex flex-col gap-3">
      <div className="font-xl font-semibold">Today's Appointments</div>
      <div>
        <ScrollArea.Autosize mah={300} mx="auto">
          {tdAppointment.map((app) => card(app))}
        </ScrollArea.Autosize>
      </div>
    </div>
  );
};

export default Appointment;
