import { useEffect, useState } from "react";
import {
  Badge,
  Card,
  Center,
  Group,
  Loader,
  SimpleGrid,
  Stack,
  Text,
  TextInput,
} from "@mantine/core";
import { IconPackage, IconSearch, IconAlertTriangle } from "@tabler/icons-react";
import { getAllStock } from "../../../Service/MedicineInventoryService";
import { getAllMedicines } from "../../../Service/MedicineService";

const LOW_STOCK_THRESHOLD = 10;

const StockOverview = () => {
  const [stock, setStock] = useState<any[]>([]);
  const [search, setSearch] = useState("");
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
      .catch((err) => console.error("Error fetching stock:", err))
      .finally(() => setLoading(false));
  }, []);

  const filtered = stock.filter((item: any) =>
    (item.medicineName ?? item.name ?? "").toLowerCase().includes(search.toLowerCase())
  );

  const lowStockCount = stock.filter(
    (item: any) => (item.quantity ?? item.stockQuantity ?? 0) <= LOW_STOCK_THRESHOLD
  ).length;

  if (loading) return <Center h={300}><Loader color="primary" /></Center>;

  return (
    <Stack gap="md">
      {lowStockCount > 0 && (
        <Card withBorder radius="md" p="sm" bg="red.0" style={{ borderColor: "#fca5a5" }}>
          <Group gap={8}>
            <IconAlertTriangle size={18} className="text-red-500" />
            <Text size="sm" fw={600} c="red">
              {lowStockCount} medicine{lowStockCount > 1 ? "s" : ""} running low on stock
            </Text>
          </Group>
        </Card>
      )}

      <TextInput
        placeholder="Search medicine..."
        leftSection={<IconSearch size={16} />}
        value={search}
        onChange={(e) => setSearch(e.currentTarget.value)}
        radius="md"
      />

      {filtered.length === 0 ? (
        <Center h={200}>
          <Stack align="center" gap={8}>
            <IconPackage size={48} stroke={1.2} className="text-gray-300" />
            <Text c="dimmed">No stock items found.</Text>
          </Stack>
        </Center>
      ) : (
        <SimpleGrid cols={{ base: 1, sm: 2, md: 3 }} spacing="md">
          {filtered.map((item: any, index: number) => {
            const name = item.medicineName ?? item.name ?? `Item #${index + 1}`;
            const qty = item.quantity ?? item.stockQuantity ?? 0;
            const isLow = qty <= LOW_STOCK_THRESHOLD;

            return (
              <Card key={item.id ?? index} withBorder radius="md" p="md" shadow="xs"
                style={isLow ? { borderColor: "#fca5a5" } : undefined}>
                <Group justify="space-between" mb={6}>
                  <Group gap={8}>
                    <div className={`p-1.5 rounded-md ${isLow ? "bg-red-50" : "bg-primary-50"}`}>
                      <IconPackage size={16} className={isLow ? "text-red-500" : "text-primary-500"} />
                    </div>
                    <Text fw={600} size="sm" lineClamp={1}>{name}</Text>
                  </Group>
                  {isLow && (
                    <Badge color="red" variant="light" size="xs">Low</Badge>
                  )}
                </Group>

                <Group gap="xs" mt={4}>
                  <Text size="xs" c="dimmed">Quantity:</Text>
                  <Text size="sm" fw={700} c={isLow ? "red" : "primary"}>{qty}</Text>
                </Group>

                {item.category && (
                  <Badge variant="light" color="gray" size="xs" mt={4}>{item.category}</Badge>
                )}
              </Card>
            );
          })}
        </SimpleGrid>
      )}
    </Stack>
  );
};

export default StockOverview;
