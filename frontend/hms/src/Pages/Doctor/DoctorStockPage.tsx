import { Group } from "@mantine/core";
import { IconPackage } from "@tabler/icons-react";
import StockOverview from "../../Components/Doctor/StockOverview/StockOverview";

const DoctorStockPage = () => {
  return (
    <div className="p-5">
      <Group gap={8} mb="lg">
        <IconPackage size={26} className="text-primary-500" />
        <h2 className="text-2xl font-semibold text-primary-600">Stock Overview</h2>
      </Group>
      <StockOverview />
    </div>
  );
};

export default DoctorStockPage;
