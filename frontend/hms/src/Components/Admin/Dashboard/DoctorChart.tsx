
import {
  doctors,

} from "../../../Data/DashboardData";

import { ScrollArea } from "@mantine/core";

const DoctorChart = () => {
  const card = (app: any) => {
    return (
      <div className="p-3 m-3 border rounded-xl justify-between border-l-4
       border-blue-500 shadow-md flex bg-blue-100" key={app.id}>
        <div>
          <div className="font-semibold">{app.name}</div>
          <div className="text-sm text-gray-500">{app.email}</div>
        </div>
        <div className="text-right">
          <div className="text-sm text-gray-500">{app.location}</div>
          <div className="text-sm text-gray-500"> {app.department}</div>
        </div>
      </div>
    );
  };

  return (
    <div className="p-3 border rounded-xl bg-blue-50 shadow-xl flex flex-col gap-3">
      <div className="font-xl font-semibold"> Doctors</div>
      <div>
        <ScrollArea.Autosize mah={300} mx="auto">
          {doctors.map((app) => card(app))}
        </ScrollArea.Autosize>
      </div>
    </div>
  );
};


export default DoctorChart
