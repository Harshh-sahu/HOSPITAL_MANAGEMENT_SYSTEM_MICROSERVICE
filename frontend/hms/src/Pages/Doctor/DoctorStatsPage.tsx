import { Group } from "@mantine/core";
import { IconChartDonut } from "@tabler/icons-react";
import DoctorStats from "../../Components/Doctor/Stats/DoctorStats";

const DoctorStatsPage = () => {
  return (
    <div className="p-5">
      <Group gap={8} mb="lg">
        <IconChartDonut size={26} className="text-primary-500" />
        <h2 className="text-2xl font-semibold text-primary-600">My Statistics</h2>
      </Group>
      <DoctorStats />
    </div>
  );
};

export default DoctorStatsPage;
