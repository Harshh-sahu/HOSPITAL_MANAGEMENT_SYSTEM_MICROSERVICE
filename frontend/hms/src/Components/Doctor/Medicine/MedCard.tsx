import {
  IconCurrencyRupee,
  IconMedicineSyrup,

  IconPill,

  IconStack2,
  IconVaccine,
} from "@tabler/icons-react";
const MedCard = ({
  name,
  dosage,
  id,
  manufacturer,
  stock,
  category,
  type,
  unitPrice,

}: any) => {

  return (
    <div
     
      className="border p-4 flex flex-col gap-2 hover:bg-primary-50 transition duration-300 rounded-xl shadow-sm  hover:shadow-[0_0_5px_1px_blue] !shadow-primary-600 ease-in-out cursor-pointer space-y-2"
    >
      <div className="flex  text-xs items-center gap-2">
        <IconPill className="text-primary-700 bg-primary-100 rounded-full" />
        <div>
          {name}  <span className="text-gray-500">      ({manufacturer})</span>
        </div>
      </div>
      <div className="flex  text-xs items-center gap-2">
        <IconPill className="text-primary-700 bg-primary-100 rounded-full" />
        <div> {dosage}</div>
      </div>

      <div className="flex  text-xs items-center gap-2">
        {" "}
        <IconStack2 className="text-primary-700 bg-primary-100 rounded-full" />
        <div>Stock: {stock}</div>
      </div>
      <div className="flex  text-xs items-center gap-2">
        <IconMedicineSyrup className="text-primary-700 bg-primary-100 rounded-full" />
        <div>{category}</div>
      </div>
            <div className="flex  text-xs items-center gap-2">
        <IconVaccine className="text-primary-700 bg-primary-100 rounded-full" />
        <div>{type}</div>
      </div>
      <div className="flex  text-xs items-center gap-2">
        <IconCurrencyRupee className="text-primary-700 bg-primary-100 rounded-full" />
        <div>Price: {unitPrice}</div>
      </div>

      
    </div>
  );
};

export default MedCard;
