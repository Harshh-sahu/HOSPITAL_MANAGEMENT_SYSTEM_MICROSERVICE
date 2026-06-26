import { useEffect, useState } from "react";
import {
  Stack, Card, Text, Group, Loader, Center, Button, Badge,
  Divider, ThemeIcon, SimpleGrid, ScrollArea, ActionIcon, Tooltip,
} from "@mantine/core";
import {
  IconBrain, IconRefresh, IconSparkles, IconAlertCircle,
  IconUsers, IconCalendarStats, IconClipboardList, IconPackage,
  IconAlertTriangle, IconStethoscope,
} from "@tabler/icons-react";
import {
  countAllAppointments, countAllReasons, getTodaysAppointments,
  getAllPrescriptions,
} from "../../../Service/AppointmentService";
import { getAllPatient } from "../../../Service/PatientProfileService";
import { getAllDoctor } from "../../../Service/DoctorProfileService";
import { getAllStock } from "../../../Service/MedicineInventoryService";
import { getAllMedicines } from "../../../Service/MedicineService";
import { getAllsales } from "../../../Service/SalesService";
import { sendChatMessage } from "../../../Service/ChatService";

const CONV_KEY = "hms_admin_insights_full_id";
const CACHE_KEY = "hms_admin_insights_full_text";

function getConvId() {
  let id = localStorage.getItem(CONV_KEY);
  if (!id) { id = `admin-full-${Date.now()}`; localStorage.setItem(CONV_KEY, id); }
  return id;
}

const AdminInsightsComponent = () => {
  const [insights, setInsights] = useState<string | null>(() => localStorage.getItem(CACHE_KEY));
  const [aiLoading, setAiLoading] = useState(false);
  const [aiError, setAiError] = useState(false);
  const [dataLoading, setDataLoading] = useState(true);
  const [lastRefreshed, setLastRefreshed] = useState<Date | null>(null);

  const [stats, setStats] = useState({
    patients: 0, doctors: 0, totalAppts: 0, todayAppts: 0,
    prescriptions: 0, lowStock: 0, revenue: 0, topReasons: [] as string[],
    lowStockItems: [] as any[],
  });

  const loadData = async () => {
    setDataLoading(true);
    const [appts, reasons, patients, doctors, today, presc, stock, sales, medicines] = await Promise.allSettled([
      countAllAppointments(), countAllReasons(), getAllPatient(), getAllDoctor(),
      getTodaysAppointments(), getAllPrescriptions(), getAllStock(), getAllsales(), getAllMedicines(),
    ]);

    const medMap = new Map(
      medicines.status === "fulfilled" ? medicines.value.map((m: any) => [m.id, m.name]) : []
    );
    const rawLow = stock.status === "fulfilled"
      ? stock.value.filter((s: any) => (s.quantity ?? 0) <= 10) : [];
    const lowStockItems = rawLow.map((s: any) => ({
      ...s,
      medicineName: medMap.get(s.medicineId) ?? `Medicine #${s.medicineId}`,
    }));
    const revenue = sales.status === "fulfilled"
      ? sales.value.reduce((s: number, sale: any) => s + (sale.totalAmount ?? sale.amount ?? 0), 0) : 0;

    setStats({
      patients: patients.status === "fulfilled" ? patients.value.length : 0,
      doctors: doctors.status === "fulfilled" ? doctors.value.length : 0,
      totalAppts: appts.status === "fulfilled" ? appts.value.reduce((s: number, m: any) => s + (m.count ?? 0), 0) : 0,
      todayAppts: today.status === "fulfilled" ? today.value.length : 0,
      prescriptions: presc.status === "fulfilled" ? presc.value.length : 0,
      lowStock: lowStockItems.length,
      revenue,
      topReasons: reasons.status === "fulfilled"
        ? reasons.value.slice(0, 5).map((r: any) => `${r.reason} (${r.count})`) : [],
      lowStockItems,
    });
    setDataLoading(false);
  };

  const fetchAI = async () => {
    setAiLoading(true); setAiError(false);
    const prompt = `You are a hospital management AI advisor. Analyze this hospital's performance and provide a comprehensive report with:

1. **Executive Summary** (2-3 sentences about overall hospital performance)
2. **Strengths** (2-3 bullet points with ✅)
3. **Areas for Improvement** (2-3 bullet points with ⚠️)
4. **Operational Recommendations** (3-4 actionable bullet points with 💡)
5. **Urgent Actions** if any (especially for low stock or high patient load)

Hospital Data:
- Registered patients: ${stats.patients} | Registered doctors: ${stats.doctors}
- Total appointments: ${stats.totalAppts} | Today's appointments: ${stats.todayAppts}
- Prescriptions issued: ${stats.prescriptions}
- Total pharmacy revenue: ₹${stats.revenue.toLocaleString("en-IN")}
- Medicines with critically low stock: ${stats.lowStock}
- Top visit reasons: ${stats.topReasons.join(", ") || "N/A"}
- Patient-to-doctor ratio: ${stats.doctors > 0 ? (stats.patients / stats.doctors).toFixed(1) : "N/A"}:1

Be professional, data-driven, and concise. Keep total under 350 words.`;

    try {
      const result = await sendChatMessage({ message: prompt, conversationId: getConvId(), role: "ADMIN", userName: "Admin" });
      setInsights(result.response);
      localStorage.setItem(CACHE_KEY, result.response);
      setLastRefreshed(new Date());
    } catch { setAiError(true); }
    finally { setAiLoading(false); }
  };

  useEffect(() => { loadData(); }, []);
  useEffect(() => { if (!dataLoading && !insights) fetchAI(); }, [dataLoading]);

  if (dataLoading) return <Center h={400}><Loader color="blue" size="lg" /></Center>;

  return (
    <Stack gap="xl">

      {/* Summary cards */}
      <SimpleGrid cols={{ base: 2, sm: 4 }} spacing="md">
        {[
          { label: "Patients", value: stats.patients, icon: <IconUsers size={20} />, c: "text-green-600", bg: "bg-green-50" },
          { label: "Doctors", value: stats.doctors, icon: <IconStethoscope size={20} />, c: "text-blue-600", bg: "bg-blue-50" },
          { label: "Total Appointments", value: stats.totalAppts, icon: <IconCalendarStats size={20} />, c: "text-violet-600", bg: "bg-violet-50" },
          { label: "Prescriptions", value: stats.prescriptions, icon: <IconClipboardList size={20} />, c: "text-teal-600", bg: "bg-teal-50" },
        ].map((s) => (
          <Card key={s.label} withBorder radius="md" p="md" shadow="xs" className={s.bg}>
            <Group justify="space-between">
              <div>
                <Text size="xs" c="dimmed" fw={500} tt="uppercase">{s.label}</Text>
                <Text size="1.6rem" fw={700} className={s.c}>{s.value}</Text>
              </div>
              <div className={`p-2 rounded-xl ${s.bg} text-gray-400`}>{s.icon}</div>
            </Group>
          </Card>
        ))}
      </SimpleGrid>

      {/* Low stock alert */}
      {stats.lowStockItems.length > 0 && (
        <Card withBorder radius="md" p="md" shadow="xs" className="bg-red-50" style={{ borderColor: "#fca5a5" }}>
          <Group gap={8} mb="sm">
            <IconAlertTriangle size={20} className="text-red-500" />
            <Text fw={700} c="red.7">{stats.lowStockItems.length} Medicine{stats.lowStockItems.length > 1 ? "s" : ""} Critically Low on Stock</Text>
          </Group>
          <SimpleGrid cols={{ base: 2, sm: 3, md: 4 }} spacing="xs">
            {stats.lowStockItems.map((item: any, i: number) => (
              <div key={i} className="flex items-center justify-between bg-white rounded-lg px-3 py-1.5 border border-red-200">
                <Group gap={6}>
                  <IconPackage size={14} className="text-red-400" />
                  <Text size="xs" fw={500} lineClamp={1}>{item.medicineName ?? item.name ?? "—"}</Text>
                </Group>
                <Badge color="red" variant="filled" size="xs">{item.quantity ?? item.stockQuantity ?? 0}</Badge>
              </div>
            ))}
          </SimpleGrid>
        </Card>
      )}

      {/* AI Analysis */}
      <Card withBorder radius="md" p="lg" shadow="xs"
        className="bg-gradient-to-br from-slate-50 to-blue-50"
        style={{ borderColor: "#93c5fd" }}>
        <Group justify="space-between" mb="md">
          <Group gap={10}>
            <ThemeIcon size="lg" radius="xl" variant="gradient" gradient={{ from: "blue", to: "cyan" }}>
              <IconBrain size={18} />
            </ThemeIcon>
            <div>
              <Text fw={700}>AI Hospital Analysis</Text>
              <Text size="xs" c="dimmed">Comprehensive performance report</Text>
            </div>
          </Group>
          <Group gap={6}>
            {lastRefreshed && (
              <Badge variant="light" color="blue" size="xs">
                {lastRefreshed.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
              </Badge>
            )}
            <Tooltip label="Refresh analysis" withArrow>
              <ActionIcon variant="light" color="blue" onClick={fetchAI} loading={aiLoading}>
                <IconRefresh size={16} />
              </ActionIcon>
            </Tooltip>
          </Group>
        </Group>
        <Divider mb="md" color="blue.2" />
        {aiLoading ? (
          <Center py="xl"><Stack align="center" gap={8}>
            <Loader color="blue" type="dots" />
            <Text size="sm" c="dimmed">Generating hospital analysis…</Text>
          </Stack></Center>
        ) : aiError ? (
          <Center py="xl"><Stack align="center" gap={8}>
            <IconAlertCircle size={36} className="text-gray-300" />
            <Text c="dimmed" size="sm">AI service unavailable.</Text>
            <Button size="xs" variant="light" color="blue" onClick={fetchAI}>Retry</Button>
          </Stack></Center>
        ) : insights ? (
          <ScrollArea mah={400}>
            <Stack gap="xs">
              {insights.split("\n").map(l => l.trim()).filter(l => l.length > 0).map((line, i) => (
                <Group key={i} gap={8} align="flex-start">
                  {!line.match(/^[\p{Emoji}•\-*#\d]/u) && (
                    <IconSparkles size={14} className="text-blue-400 mt-0.5 shrink-0" />
                  )}
                  <Text size="sm" style={{ lineHeight: 1.7 }} fw={line.startsWith("**") ? 600 : 400}>
                    {line.replace(/\*\*/g, "")}
                  </Text>
                </Group>
              ))}
            </Stack>
          </ScrollArea>
        ) : (
          <Center py="xl">
            <Button size="xs" variant="light" color="blue" onClick={fetchAI}>Generate Analysis</Button>
          </Center>
        )}
      </Card>

    </Stack>
  );
};

export default AdminInsightsComponent;
