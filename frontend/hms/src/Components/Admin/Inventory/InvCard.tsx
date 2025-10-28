import {
  IconCurrencyRupee,
  IconMedicineSyrup,

  IconPill,

  IconStack2,
  IconVaccine,
} from "@tabler/icons-react";
import { formatDate } from "../../../Utility/DateUtility";
const InvCard = ({

  id,
  medicineId,
  quantity,
  initialQuantity,
  status,
  batchNo,
  expiryDate,
  medicineMap,
  onEdit
}: any) => {

  return (
    <div
      onClick={onEdit}
      className="border p-4 flex flex-col gap-2 hover:bg-primary-50 transition duration-300 rounded-xl shadow-sm  hover:shadow-[0_0_5px_1px_blue] !shadow-primary-600 ease-in-out cursor-pointer space-y-2"
    >
      <div className="flex  text-xs items-center gap-2">
        <IconPill className="text-primary-700 bg-primary-100 rounded-full" />
        <div>
          {medicineMap[medicineId]?.name}  <span className="text-gray-500">      ({medicineMap[medicineId]?.manufacturer})</span>
        </div>
      </div>
      <div className="flex  text-xs items-center gap-2">
        <IconPill className="text-primary-700 bg-primary-100 rounded-full" />
        <div> {batchNo}</div>
      </div>
       <div className="flex  text-xs items-center gap-2">
        <IconVaccine className="text-primary-700 bg-primary-100 rounded-full" />
        <div> {formatDate(expiryDate)}</div>
      </div>

      <div className="flex  text-xs items-center gap-2">
        {" "}
        <IconStack2 className="text-primary-700 bg-primary-100 rounded-full" />
        <div>Stock: {quantity}</div>
      </div>
      <div className="flex  text-xs items-center gap-2">
        <IconMedicineSyrup className="text-primary-700 bg-primary-100 rounded-full" />
        <div>{status}</div>
        </div>

      
    </div>
  );
};

export default InvCard;
