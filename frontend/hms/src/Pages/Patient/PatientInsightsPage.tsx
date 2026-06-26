import { Group } from "@mantine/core";
import { IconBrain } from "@tabler/icons-react";
import PatientInsightsComponent from "../../Components/Patient/Insights/PatientInsightsComponent";

const PatientInsightsPage = () => {
  return (
    <div className="p-5">
      <Group gap={8} mb="lg">
        <IconBrain size={26} className="text-violet-500" />
        <h2 className="text-2xl font-semibold text-violet-600">Health Insights</h2>
      </Group>
      <PatientInsightsComponent />
    </div>
  );
};

export default PatientInsightsPage;
