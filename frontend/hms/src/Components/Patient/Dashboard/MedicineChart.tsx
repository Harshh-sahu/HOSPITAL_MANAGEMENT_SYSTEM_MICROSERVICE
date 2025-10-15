
import {
  medicines,

} from "../../../Data/DashboardData";

import { ScrollArea } from "@mantine/core";

const MedicineChart = () => {
  const card = (app: any) => {
    return (
      <div className="p-3 m-3 border rounded-xl justify-between border-l-4
       border-orange-500 shadow-md flex bg-orange-100 items-center" key={app.id}>
        <div>
          <div className="font-semibold">{app.name}</div>
          <div className="text-sm text-gray-500">{app.manufacturer}</div>
        </div>
        <div className="text-right">
          <div className="text-sm text-gray-500">{app.dosage}</div>
        </div>
      </div>
    );
  };
 
  return (
    <div className="p-3 border rounded-xl bg-orange-50 shadow-xl flex flex-col gap-3">
      <div className="font-xl font-semibold"> Medicines</div>
      <div>
        <ScrollArea.Autosize mah={300} mx="auto">
          {medicines.map((app) => card(app))}
        </ScrollArea.Autosize>
      </div>
    </div>
  );
};

export default MedicineChart
