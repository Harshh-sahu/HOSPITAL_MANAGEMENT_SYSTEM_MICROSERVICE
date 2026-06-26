import { Group } from "@mantine/core";
import { IconChartBar } from "@tabler/icons-react";
import AdminStatsComponent from "../../Components/Admin/Stats/AdminStatsComponent";

const AdminStatsPage = () => (
  <div className="p-5">
    <Group gap={8} mb="lg">
      <IconChartBar size={26} className="text-blue-500" />
      <h2 className="text-2xl font-semibold text-blue-600">Hospital Statistics</h2>
    </Group>
    <AdminStatsComponent />
  </div>
);

export default AdminStatsPage;
