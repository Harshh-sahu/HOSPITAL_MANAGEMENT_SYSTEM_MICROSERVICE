import {
  IconClock,
  IconMedicineSyrup,
  IconNote,
  IconQuestionMark,
  IconUserHeart,
} from "@tabler/icons-react";
import { formatDate, formatDateWithTime } from "../../../Utility/DateUtility";
const ReportCard = ({id,
appointmentId,
doctorName,
notes,
createdAt,
diagnosis
}: any) => {




  return (
    <div className="border p-4 flex flex-col gap-2 hover:bg-primary-50 transition duration-300 rounded-xl shadow-sm  hover:shadow-[0_0_5px_1px_blue] !shadow-primary-600 ease-in-out cursor-pointer space-y-2">
    

      <div className="flex  text-xs items-center gap-2">
        <IconUserHeart className="text-primary-700 bg-primary-100 rounded-full" />
        <div>{doctorName}</div>
      </div>

            <div className="flex  text-xs items-center gap-2">
        <IconQuestionMark className="text-primary-700 bg-primary-100 rounded-full" />
        <div>{diagnosis}</div>
      </div>


      <div className="flex  text-xs items-center gap-2">
        <IconClock className="text-primary-700 bg-primary-100 rounded-full" />
        <div>{formatDate(createdAt)}</div>
      </div>
           


 { notes &&    <div className="flex  text-xs items-center gap-2">
        <IconNote className="text-primary-700 bg-primary-100 rounded-full" />
        <div>{notes}</div>
      </div>}
    </div>
  );
};

export default ReportCard;
