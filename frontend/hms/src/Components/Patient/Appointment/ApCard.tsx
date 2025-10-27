import {
  IconClock,
  IconEmergencyBed,
  IconNote,
  IconProgress,
  IconUserHeart,
} from "@tabler/icons-react";
import { formatDateWithTime } from "../../../Utility/DateUtility";
import { Tag } from "primereact/tag";
const ApCard = ({
id,
doctorName,
doctorId,
notes,
reason,status,
appointmentTime,
}: any) => {
  const getSeverity = (status: string) => {
    switch (status) {
      case "CANCELLED":
        return "danger";

      case "COMPLETED":
        return "success";

      case "SCHEDULED":
        return "info";

      case "negotiation":
        return "warning";

      default:
        return null;
    }
  };
  return (
    <div className="border p-4 flex flex-col gap-2 hover:bg-primary-50 transition duration-300 rounded-xl shadow-sm  hover:shadow-[0_0_5px_1px_blue] !shadow-primary-600 ease-in-out cursor-pointer space-y-2">
    

      <div className="flex  text-xs items-center gap-2">
        <IconUserHeart className="text-primary-700 bg-primary-100 rounded-full" />
        <div>{doctorName}</div>
      </div>

      <div className="flex  text-xs items-center gap-2">
        <IconNote className="text-primary-700 bg-primary-100 rounded-full" />
        <div>{notes}</div>
      </div>
      <div className="flex  text-xs items-center gap-2">
        <IconEmergencyBed className="text-primary-700 bg-primary-100 rounded-full" />
        <div>+91 {reason}</div>
      </div>
      <div className="flex  text-xs items-center gap-2">
        <IconClock className="text-primary-700 bg-primary-100 rounded-full" />
        <div>{formatDateWithTime(appointmentTime)}</div>
      </div>

      <div className="flex  text-xs items-center gap-2">
        <IconProgress className="text-primary-700 bg-primary-100 rounded-full" />
      <Tag value={status} severity={getSeverity(status)}/>
      </div>
    </div>
  );
};

export default ApCard;
