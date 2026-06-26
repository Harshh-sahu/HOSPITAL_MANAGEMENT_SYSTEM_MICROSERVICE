import { Group } from "@mantine/core";
import { IconClipboardList } from "@tabler/icons-react";
import PrescriptionsHistory from "../../Components/Doctor/PrescriptionsHistory/PrescriptionsHistory";

const DoctorPrescriptionsPage = () => {
  return (
    <div className="p-5">
      <Group gap={8} mb="lg">
        <IconClipboardList size={26} className="text-primary-500" />
        <h2 className="text-2xl font-semibold text-primary-600">Prescriptions History</h2>
      </Group>
      <PrescriptionsHistory />
    </div>
  );
};

export default DoctorPrescriptionsPage;
