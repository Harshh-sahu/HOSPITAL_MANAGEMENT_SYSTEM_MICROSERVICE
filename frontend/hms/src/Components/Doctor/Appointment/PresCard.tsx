import {
  IconClock,
  IconEmergencyBed,
  IconMedicineSyrup,
  IconNote,
  IconProgress,
  IconUserHeart,
} from "@tabler/icons-react";
import { formatDateWithTime } from "../../../Utility/DateUtility";
import { Button } from "@mantine/core";
import { useNavigate } from "react-router-dom";
const PresCard = ({
appointmentId,
doctorName,
notes,
reason,status,
prescriptionDate,
medicines,
handleMedicine
}: any) => {

  const navigate = useNavigate();

  return (
    <div onClick={()=> navigate("/doctor/appointment/"+appointmentId)} className="border p-4 flex flex-col gap-2 hover:bg-primary-50 transition duration-300 rounded-xl shadow-sm  hover:shadow-[0_0_5px_1px_blue] !shadow-primary-600 ease-in-out cursor-pointer space-y-2">
    

      <div className="flex  text-xs items-center gap-2">
        <IconUserHeart className="text-primary-700 bg-primary-100 rounded-full" />
        <div>{doctorName}</div>
      </div>

      <div className="flex  text-xs items-center gap-2">
        <IconClock className="text-primary-700 bg-primary-100 rounded-full" />
        <div>{formatDateWithTime(prescriptionDate)}</div>
      </div>
            <div className="flex  text-xs items-center gap-2">
        <IconMedicineSyrup className="text-primary-700 bg-primary-100 rounded-full" />
        <div className="flex gap-5 items-center">{medicines?.length} <Button onClick={()=> handleMedicine(medicines)} size="compact-xs">view medicine</Button></div>
      </div>


 { notes &&    <div className="flex  text-xs items-center gap-2">
        <IconNote className="text-primary-700 bg-primary-100 rounded-full" />
        <div>{notes}</div>
      </div>}
    </div>
  );
};

export default PresCard;
