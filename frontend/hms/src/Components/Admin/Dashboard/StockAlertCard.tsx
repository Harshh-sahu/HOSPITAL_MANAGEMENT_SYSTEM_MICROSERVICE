import { useEffect, useState } from "react";
import { Card, Group, Text, Badge, SimpleGrid, Loader, Center } from "@mantine/core";
import { IconAlertTriangle, IconPackage, IconCircleCheck } from "@tabler/icons-react";
import { getAllStock } from "../../../Service/MedicineInventoryService";
import { getAllMedicines } from "../../../Service/MedicineService";

const LOW = 10;

const StockAlertCard = () => {
  const [stock, setStock] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.allSettled([getAllStock(), getAllMedicines()])
      .then(([stockRes, medRes]) => {
        const rawStock = stockRes.status === "fulfilled" ? stockRes.value : [];
        const medicines: any[] = medRes.status === "fulfilled" ? medRes.value : [];
        const medMap = new Map(medicines.map((m: any) => [m.id, m.name]));
        const enriched = rawStock.map((s: any) => ({
          ...s,
          medicineName: medMap.get(s.medicineId) ?? `Medicine #${s.medicineId}`,
        }));
        setStock(enriched);
      })
      .catch(() => setStock([]))
      .finally(() => setLoading(false));
  }, []);

  const lowStock = stock.filter((s) => (s.quantity ?? 0) <= LOW);
  const okCount = stock.length - lowStock.length;

  if (loading) return <Center h={80}><Loader size="sm" color="red" /></Center>;

  if (lowStock.length === 0) {
    return (
      <Card withBorder radius="md" p="md" shadow="xs" className="bg-green-50" style={{ borderColor: "#86efac" }}>
        <Group gap={10}>
          <IconCircleCheck size={22} className="text-green-500" />
          <Text fw={600} c="green.7">All {stock.length} medicines are well-stocked</Text>
        </Group>
      </Card>
    );
  }

  return (
    <Card withBorder radius="md" p="md" shadow="xs" className="bg-red-50" style={{ borderColor: "#fca5a5" }}>
      <Group justify="space-between" mb="sm">
        <Group gap={8}>
          <IconAlertTriangle size={20} className="text-red-500" />
          <Text fw={700} c="red.7">{lowStock.length} Medicine{lowStock.length > 1 ? "s" : ""} Running Low</Text>
        </Group>
        <Badge color="green" variant="light" size="sm">{okCount} OK</Badge>
      </Group>
      <SimpleGrid cols={{ base: 2, sm: 3, md: 4 }} spacing="xs">
        {lowStock.map((item, i) => (
          <div key={item.id ?? i} className="flex items-center justify-between bg-white rounded-lg px-3 py-1.5 border border-red-200">
            <Group gap={6}>
              <IconPackage size={14} className="text-red-400" />
              <Text size="xs" fw={500} lineClamp={1}>{item.medicineName ?? item.name ?? "—"}</Text>
            </Group>
            <Badge color="red" variant="filled" size="xs">{item.quantity ?? item.stockQuantity ?? 0}</Badge>
          </div>
        ))}
      </SimpleGrid>
    </Card>
  );
};

export default StockAlertCard;
