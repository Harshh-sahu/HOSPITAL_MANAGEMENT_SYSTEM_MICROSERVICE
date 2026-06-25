
import { useEffect, useState } from "react";
import { Center, ScrollArea, Text } from "@mantine/core";
import { IconCalendarCheck } from "@tabler/icons-react";
import { getTodaysAppointments } from "../../../Service/AppointmentService";
import { extractTimein12HourFormat } from "../../../Utility/DateUtility";

const Appointment = () => {
   const[tdAppointment,setTdAppointment]=useState<any[]>([]);
  
    useEffect(()=>{
      getTodaysAppointments().then((res)=>{
        setTdAppointment(res);
      }).catch((err)=>{
        console.error("Error fetching today's appointments:", err);
      });
    },[]);

  const card = (app: any) => {
    return (
      <div className="p-3 m-3 border rounded-xl items-center justify-between border-l-4
       border-violet-500 shadow-md flex bg-violet-100" key={app.id}>
        <div className="">
          <div className="font-semibold">{app.patientName}</div>
          <div className="text-sm text-gray-500">{app.reason}</div>
        </div>
        <div className="text-right">
          <div className="text-sm text-gray-500">{extractTimein12HourFormat(app.appointmentTime)}</div>
        </div>
      </div>
    );
  };

  return (
    <div className="p-3 border rounded-xl bg-violet-50 shadow-xl flex flex-col gap-3">
      <div className="flex items-center gap-2">
        <IconCalendarCheck size={18} className="text-violet-500" />
        <div className="font-semibold">Today's Appointments</div>
        <span className="ml-auto text-xs font-medium text-violet-600 bg-violet-100 px-2 py-0.5 rounded-full">
          {tdAppointment.length}
        </span>
      </div>
      <div>
        <ScrollArea.Autosize mah={300} mx="auto">
          {tdAppointment.length === 0 ? (
            <Center py="xl">
              <Text size="sm" c="dimmed">No appointments scheduled for today.</Text>
            </Center>
          ) : (
            tdAppointment.map((app) => card(app))
          )}
        </ScrollArea.Autosize>
      </div>
    </div>
  );
};

export default Appointment;
