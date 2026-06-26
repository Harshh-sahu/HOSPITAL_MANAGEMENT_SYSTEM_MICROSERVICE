import { Group } from "@mantine/core";
import { IconBrain } from "@tabler/icons-react";
import AdminInsightsComponent from "../../Components/Admin/Insights/AdminInsightsComponent";

const AdminInsightsPage = () => (
  <div className="p-5">
    <Group gap={8} mb="lg">
      <IconBrain size={26} className="text-blue-500" />
      <h2 className="text-2xl font-semibold text-blue-600">AI Hospital Insights</h2>
    </Group>
    <AdminInsightsComponent />
  </div>
);

export default AdminInsightsPage;
