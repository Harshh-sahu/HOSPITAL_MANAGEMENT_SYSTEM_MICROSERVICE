import { Group } from "@mantine/core";
import { IconPill } from "@tabler/icons-react";
import MedicineReference from "../../Components/Doctor/MedicineReference/MedicineReference";

const DoctorMedicineReferencePage = () => {
  return (
    <div className="p-5">
      <Group gap={8} mb="lg">
        <IconPill size={26} className="text-primary-500" />
        <h2 className="text-2xl font-semibold text-primary-600">Medicine Reference</h2>
      </Group>
      <MedicineReference />
    </div>
  );
};

export default DoctorMedicineReferencePage;
