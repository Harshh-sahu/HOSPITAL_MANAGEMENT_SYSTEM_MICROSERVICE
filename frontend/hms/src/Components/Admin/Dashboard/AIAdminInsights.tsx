import { useEffect, useState } from "react";
import {
  Card, Text, Group, Loader, Center, Stack,
  Button, Badge, Divider, ThemeIcon, ActionIcon, Tooltip, ScrollArea,
} from "@mantine/core";
import { IconBrain, IconRefresh, IconSparkles, IconAlertCircle } from "@tabler/icons-react";
import {
  countAllAppointments, countAllReasons, getTodaysAppointments, getAllPrescriptions,
} from "../../../Service/AppointmentService";
import { getAllPatient } from "../../../Service/PatientProfileService";
import { getAllDoctor } from "../../../Service/DoctorProfileService";
import { getAllStock } from "../../../Service/MedicineInventoryService";
import { sendChatMessage } from "../../../Service/ChatService";

const INSIGHTS_KEY = "hms_admin_insights_id";
const CACHE_KEY = "hms_admin_insights_text";

function getConvId() {
  let id = localStorage.getItem(INSIGHTS_KEY);
  if (!id) {
    id = `admin-insights-${Date.now()}`;
    localStorage.setItem(INSIGHTS_KEY, id);
  }
  return id;
}

const AIAdminInsights = () => {
  const [insights, setInsights] = useState<string | null>(() => localStorage.getItem(CACHE_KEY));
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(false);
  const [lastRefreshed, setLastRefreshed] = useState<Date | null>(null);

  const fetchInsights = async () => {
    setLoading(true);
    setError(false);
    try {
      const [appts, reasons, patients, doctors, today, prescriptions, stock] = await Promise.allSettled([
        countAllAppointments(),
        countAllReasons(),
        getAllPatient(),
        getAllDoctor(),
        getTodaysAppointments(),
        getAllPrescriptions(),
        getAllStock(),
      ]);

      const totalAppts = appts.status === "fulfilled"
        ? appts.value.reduce((s: number, m: any) => s + (m.count ?? 0), 0) : 0;
      const topReasons = reasons.status === "fulfilled"
        ? reasons.value.slice(0, 4).map((r: any) => `${r.reason}(${r.count})`).join(", ") : "N/A";
      const patientCount = patients.status === "fulfilled" ? patients.value.length : 0;
      const doctorCount = doctors.status === "fulfilled" ? doctors.value.length : 0;
      const todayCount = today.status === "fulfilled" ? today.value.length : 0;
      const prescCount = prescriptions.status === "fulfilled" ? prescriptions.value.length : 0;
      const lowStock = stock.status === "fulfilled"
        ? stock.value.filter((s: any) => (s.quantity ?? s.stockQuantity ?? 0) <= 10).length : 0;

      const prompt = `You are a hospital management analytics AI. Analyze this hospital's performance data and provide:
1. A brief overall performance summary (2-3 sentences)
2. Key metrics observations (3-4 bullet points with emojis)  
3. Actionable recommendations for hospital admin (3-4 bullet points with emojis)
4. Any urgent alerts if needed

Hospital Data:
- Total registered patients: ${patientCount}
- Total registered doctors: ${doctorCount}
- Total appointments: ${totalAppts}
- Today's appointments: ${todayCount}
- Total prescriptions issued: ${prescCount}
- Top visit reasons: ${topReasons}
- Medicines with low stock (≤10 units): ${lowStock}

Be concise, professional, and actionable. Keep total response under 250 words.`;

      const result = await sendChatMessage({
        message: prompt,
        conversationId: getConvId(),
        role: "ADMIN",
        userName: "Admin",
      });

      setInsights(result.response);
      localStorage.setItem(CACHE_KEY, result.response);
      setLastRefreshed(new Date());
    } catch {
      setError(true);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!insights) fetchInsights();
  }, []);

  return (
    <Card withBorder radius="md" p="lg" shadow="xs"
      className="bg-gradient-to-br from-slate-50 to-blue-50"
      style={{ borderColor: "#93c5fd" }}>
      <Group justify="space-between" mb="md">
        <Group gap={10}>
          <ThemeIcon size="lg" radius="xl" variant="gradient" gradient={{ from: "blue", to: "cyan" }}>
            <IconBrain size={18} />
          </ThemeIcon>
          <div>
            <Text fw={700}>AI Hospital Insights</Text>
            <Text size="xs" c="dimmed">Auto-generated performance analysis</Text>
          </div>
        </Group>
        <Group gap={6}>
          {lastRefreshed && (
            <Badge variant="light" color="blue" size="xs">
              {lastRefreshed.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
            </Badge>
          )}
          <Tooltip label="Refresh analysis" withArrow>
            <ActionIcon variant="light" color="blue" onClick={fetchInsights} loading={loading}>
              <IconRefresh size={16} />
            </ActionIcon>
          </Tooltip>
        </Group>
      </Group>
      <Divider mb="md" color="blue.2" />

      {loading ? (
        <Center py="xl">
          <Stack align="center" gap={8}>
            <Loader color="blue" type="dots" />
            <Text size="sm" c="dimmed">Analyzing hospital data…</Text>
          </Stack>
        </Center>
      ) : error ? (
        <Center py="xl">
          <Stack align="center" gap={8}>
            <IconAlertCircle size={36} className="text-gray-300" />
            <Text c="dimmed" size="sm">AI service unavailable.</Text>
            <Button size="xs" variant="light" color="blue" onClick={fetchInsights}>Retry</Button>
          </Stack>
        </Center>
      ) : insights ? (
        <ScrollArea mah={260}>
          <Stack gap="xs">
            {insights.split("\n").map(l => l.trim()).filter(l => l.length > 0).map((line, i) => (
              <Group key={i} gap={8} align="flex-start">
                {!line.match(/^[\p{Emoji}•\-*\d]/u) && (
                  <IconSparkles size={14} className="text-blue-400 mt-0.5 shrink-0" />
                )}
                <Text size="sm" style={{ lineHeight: 1.7 }}>{line}</Text>
              </Group>
            ))}
          </Stack>
        </ScrollArea>
      ) : (
        <Center py="xl">
          <Button size="xs" variant="light" color="blue" onClick={fetchInsights}>Generate Insights</Button>
        </Center>
      )}
    </Card>
  );
};

export default AIAdminInsights;
