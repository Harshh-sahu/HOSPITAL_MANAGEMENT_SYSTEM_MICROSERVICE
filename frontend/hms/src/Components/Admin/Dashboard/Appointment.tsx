import { AreaChart } from "@mantine/charts";

import {
  appointmentData,
  appointments,
  doctorData,
  patientData,
} from "../../../Data/DashboardData";
import { IconFile, IconStethoscope, IconUser } from "@tabler/icons-react";
import { ScrollArea } from "@mantine/core";

const Appointment = () => {
  const card = (app: any) => {
    return (
      <div className="p-3 m-3 border rounded-xl justify-between border-l-4
       border-violet-500 shadow-md flex bg-violet-100" key={app.id}>
        <div>
          <div className="font-semibold">{app.patient}</div>
          <div className="text-sm text-gray-500">{app.doctor}</div>
        </div>
        <div className="text-right">
          <div className="text-sm text-gray-500">{app.time}</div>
          <div className="text-sm text-gray-500">{app.region}</div>
        </div>
      </div>
    );
  };
  const cards = [
    {
      name: "Appointments",
      id: "appointments",
      color: "blue",
      bg: "bg-blue-100",
      icon: <IconFile />,
      data: appointmentData,
    },
    {
      name: "Patients",
      id: "patients",
      color: "green",
      bg: "bg-green-100",
      icon: <IconUser />,
      data: patientData,
    },
    {
      name: "Doctors",
      id: "doctors",
      color: "red",
      bg: "bg-red-100",
      icon: <IconStethoscope />,
      data: doctorData,
    },
  ];
  return (
    <div className="p-3 border rounded-xl bg-violet-50 shadow-xl flex flex-col gap-3">
      <div className="font-xl font-semibold">Today's Appointments</div>
      <div>
        <ScrollArea.Autosize mah={300} mx="auto">
          {appointments.map((app) => card(app))}
        </ScrollArea.Autosize>
      </div>
    </div>
  );
};

export default Appointment;
