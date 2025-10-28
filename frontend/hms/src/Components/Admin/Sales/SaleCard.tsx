import {
  IconClock,
  IconCoinRupee,
  IconPhone,
  IconUserHeart,
} from "@tabler/icons-react";
import { formatDate } from "../../../Utility/DateUtility";
const SaleCard = ({ buyerContact, saleDate, buyerName, totalAmount, onView}: any) => {
  return (
    <div onClick={onView} className="border p-4 flex flex-col gap-2 hover:bg-primary-50 transition duration-300 rounded-xl shadow-sm  hover:shadow-[0_0_5px_1px_blue] !shadow-primary-600 ease-in-out cursor-pointer space-y-2">
      <div className="flex  text-xs items-center gap-2">
        <IconUserHeart className="text-primary-700 bg-primary-100 rounded-full" />
        <div>{buyerName}</div>
      </div>
      <div className="flex  text-xs items-center gap-2">
        <IconPhone className="text-primary-700 bg-primary-100 rounded-full" />
        <div>+91 {buyerContact}</div>
      </div>

      <div className="flex  text-xs items-center gap-2">
        {" "}
        <IconCoinRupee className="text-primary-700 bg-primary-100 rounded-full" />
        <div>{totalAmount}</div>
      </div>
      <div className="flex  text-xs items-center gap-2">
        <IconClock className="text-primary-700 bg-primary-100 rounded-full" />
        <div>{formatDate(saleDate)}</div>
      </div>
    </div>
  );
};

export default SaleCard;
