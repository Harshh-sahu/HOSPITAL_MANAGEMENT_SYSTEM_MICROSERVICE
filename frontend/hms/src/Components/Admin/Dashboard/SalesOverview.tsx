import { useEffect, useState } from "react";
import { Card, Group, Text, Badge, SimpleGrid, Loader, Center, Stack, ThemeIcon, Divider } from "@mantine/core";
import { AreaChart } from "@mantine/charts";
import { IconReceiptRupee, IconShoppingCart, IconTrendingUp, IconShoppingBag } from "@tabler/icons-react";
import { getAllsales } from "../../../Service/SalesService";

const SalesOverview = () => {
  const [sales, setSales] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getAllsales()
      .then((res) => setSales(Array.isArray(res) ? res : []))
      .catch(() => setSales([]))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <Center h={140}><Loader size="sm" color="teal" /></Center>;

  // Group by month
  const monthMap: Record<string, number> = {};
  sales.forEach((s: any) => {
    const raw = s.saleDate ?? s.createdAt;
    if (raw) {
      const d = new Date(raw);
      const label = d.toLocaleString("default", { month: "short", year: "2-digit" });
      monthMap[label] = (monthMap[label] ?? 0) + (s.totalAmount ?? s.amount ?? 0);
    }
  });
  const chartData = Object.entries(monthMap).map(([month, total]) => ({ month, total }));
  const totalRevenue = sales.reduce((s: number, sale: any) => s + (sale.totalAmount ?? sale.amount ?? 0), 0);
  const avgSale = sales.length > 0 ? Math.round(totalRevenue / sales.length) : 0;

  return (
    <Card withBorder radius="md" p={0} shadow="xs" style={{ overflow: "hidden" }}>
      {/* Gradient header */}
      <div className="bg-gradient-to-r from-teal-500 to-emerald-500 px-5 py-4">
        <Group justify="space-between">
          <Group gap={10}>
            <ThemeIcon size="lg" radius="xl" color="white" variant="white">
              <IconTrendingUp size={18} className="text-teal-600" />
            </ThemeIcon>
            <div>
              <Text fw={700} c="white" size="md">Sales Overview</Text>
              <Text size="xs" c="rgba(255,255,255,0.8)">Monthly revenue trend</Text>
            </div>
          </Group>
          <Badge variant="white" color="teal" size="sm" radius="xl">{sales.length} sales</Badge>
        </Group>
      </div>

      <div className="p-4">
        {/* Stat cards */}
        <SimpleGrid cols={3} spacing="xs" mb="md">
          {[
            { label: "Revenue", value: `₹${totalRevenue.toLocaleString("en-IN")}`, icon: <IconReceiptRupee size={16} />, c: "teal" },
            { label: "Transactions", value: sales.length, icon: <IconShoppingCart size={16} />, c: "blue" },
            { label: "Avg Sale", value: `₹${avgSale.toLocaleString("en-IN")}`, icon: <IconShoppingBag size={16} />, c: "violet" },
          ].map((s) => (
            <div key={s.label} className="rounded-xl border border-gray-100 p-3 text-center bg-gray-50">
              <Text size="xs" c="dimmed" fw={500}>{s.label}</Text>
              <Text fw={700} size="sm" c={`${s.c}.7`}>{s.value}</Text>
            </div>
          ))}
        </SimpleGrid>

        <Divider mb="md" color="gray.1" />

        {chartData.length === 0 ? (
          <Center h={100}>
            <Stack align="center" gap={4}>
              <IconShoppingCart size={36} stroke={1.2} className="text-gray-300" />
              <Text size="sm" c="dimmed">No sales data yet.</Text>
            </Stack>
          </Center>
        ) : (
          <AreaChart
            h={150}
            data={chartData}
            dataKey="month"
            series={[{ name: "total", color: "teal.5", label: "Revenue (₹)" }]}
            curveType="natural"
            tickLine="none"
            gridAxis="y"
            withDots={false}
            fillOpacity={0.2}
          />
        )}
      </div>
    </Card>
  );
};

export default SalesOverview;

