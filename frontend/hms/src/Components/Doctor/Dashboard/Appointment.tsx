
import {
  appointments,

} from "../../../Data/DashboardData";

import { ScrollArea } from "@mantine/core";

const Appointment = () => {
  const card = (app: any) => {
    return (
      <div className="p-3 m-3 border rounded-xl items-center justify-between border-l-4
       border-violet-500 shadow-md flex bg-violet-100" key={app.id}>
        <div className="">
          <div className="font-semibold">{app.patient}</div>
          <div className="text-sm text-gray-500">{app.reason}</div>
        </div>
        <div className="text-right">
          <div className="text-sm text-gray-500">{app.time}</div>
        </div>
      </div>
    );
  };

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
