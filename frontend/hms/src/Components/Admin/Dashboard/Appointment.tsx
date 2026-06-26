import { Center, ScrollArea, Text } from "@mantine/core";
import { useEffect, useState } from "react";
import { getTodaysAppointments } from "../../../Service/AppointmentService";
import { extractTimein12HourFormat } from "../../../Utility/DateUtility";
import { IconCalendarOff } from "@tabler/icons-react";

const Appointment = () => {
  const [tdAppointment, setTdAppointment] = useState<any[]>([]);

  useEffect(() => {
    getTodaysAppointments().then((res) => {
      setTdAppointment(res);
    }).catch((err) => {
      console.error("Error fetching today's appointments:", err);
    });
  }, []);

  const card = (app: any) => (
    <div className="p-3 m-3 border rounded-xl justify-between border-l-4 border-violet-500 shadow-md flex bg-violet-100" key={app.id}>
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

  return (
    <div className="p-3 border rounded-xl bg-violet-50 shadow-xl flex flex-col gap-3">
      <div className="flex items-center gap-2">
        <span className="font-semibold">Today&apos;s Appointments</span>
        <span className="ml-auto text-xs font-medium text-violet-600 bg-violet-100 px-2 py-0.5 rounded-full">
          {tdAppointment.length}
        </span>
      </div>
      <ScrollArea.Autosize mah={300} mx="auto">
        {tdAppointment.length === 0 ? (
          <Center h={180}>
            <div className="flex flex-col items-center gap-2">
              <IconCalendarOff size={44} stroke={1.2} className="text-gray-300" />
              <Text c="dimmed" size="sm">No appointments scheduled today.</Text>
            </div>
          </Center>
        ) : tdAppointment.map((app) => card(app))}
      </ScrollArea.Autosize>
    </div>
  );
};

export default Appointment;
