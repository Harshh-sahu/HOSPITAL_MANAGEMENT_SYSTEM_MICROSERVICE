import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import {
  Card, Text, Group, Loader, Center, Stack,
  Button, Badge, Divider, ThemeIcon, ActionIcon, Tooltip,
} from "@mantine/core";
import {
  IconBrain, IconRefresh, IconSparkles,
  IconAlertCircle, IconHeartbeat,
} from "@tabler/icons-react";
import {
  countAppointmentByPatient,
  getReportByPatientId,
  getMedicineConsumeByPatient,
  getPrescriptionByPatientId,
} from "../../../Service/AppointmentService";
import { sendChatMessage } from "../../../Service/ChatService";

const INSIGHTS_CONV_KEY = "hms_ai_insights_id";

function getInsightsConvId(): string {
  let id = localStorage.getItem(INSIGHTS_CONV_KEY);
  if (!id) {
    id = `insights-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`;
    localStorage.setItem(INSIGHTS_CONV_KEY, id);
  }
  return id;
}

function buildHealthPrompt(data: {
  totalVisits: number;
  diagnoses: string[];
  medicines: string[];
  prescriptionCount: number;
  name: string;
}): string {
  return `You are a helpful health assistant. Based on my health summary below, give me 3–4 short, friendly, actionable health insights and wellness tips. Be concise, positive, and encouraging. Format each insight as a bullet point starting with an emoji.

Patient: ${data.name}
Total hospital visits: ${data.totalVisits}
Diagnoses/reasons on record: ${data.diagnoses.length ? data.diagnoses.join(", ") : "none yet"}
Current medicines: ${data.medicines.length ? data.medicines.join(", ") : "none recorded"}
Prescriptions issued: ${data.prescriptionCount}

Provide 3-4 personalized health tips based on this. Keep each tip under 2 sentences.`;
}

const AIHealthInsights = () => {
  const user = useSelector((state: any) => state.user);
  const [insights, setInsights] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(false);
  const [lastRefreshed, setLastRefreshed] = useState<Date | null>(null);

  const fetchInsights = async () => {
    const id = user?.profileId;
    if (!id) return;
    setLoading(true);
    setError(false);

    try {
      const [monthly, records, medicines, prescriptions] = await Promise.allSettled([
        countAppointmentByPatient(id),
        getReportByPatientId(id),
        getMedicineConsumeByPatient(id),
        getPrescriptionByPatientId(id),
      ]);

      const totalVisits = monthly.status === "fulfilled"
        ? monthly.value.reduce((s: number, m: any) => s + (m.count ?? 0), 0) : 0;

      const diagnoses = records.status === "fulfilled"
        ? records.value.map((r: any) => r.diagnosis).filter(Boolean) : [];

      const medNames = medicines.status === "fulfilled"
        ? medicines.value.map((m: any) => m.medicineName ?? m.name).filter(Boolean) : [];

      const prescriptionCount = prescriptions.status === "fulfilled"
        ? prescriptions.value.length : 0;

      const prompt = buildHealthPrompt({
        totalVisits,
        diagnoses,
        medicines: medNames,
        prescriptionCount,
        name: user?.name ?? "Patient",
      });

      const result = await sendChatMessage({
        message: prompt,
        conversationId: getInsightsConvId(),
        role: user?.role ?? "PATIENT",
        userName: user?.name,
        profileId: user?.profileId,
      });

      setInsights(result.response);
      setLastRefreshed(new Date());
    } catch (e) {
      setError(true);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchInsights();
  }, [user?.profileId]);

  return (
    <Card withBorder radius="md" p="lg" shadow="xs" className="bg-gradient-to-br from-indigo-50 to-purple-50"
      style={{ borderColor: "#c4b5fd" }}>
      {/* Header */}
      <Group justify="space-between" mb="md">
        <Group gap={10}>
          <ThemeIcon size="md" radius="xl" variant="gradient" gradient={{ from: "indigo", to: "violet" }}>
            <IconBrain size={16} />
          </ThemeIcon>
          <div>
            <Text fw={700} size="sm">AI Health Insights</Text>
            <Text size="xs" c="dimmed">Personalized analysis powered by AI</Text>
          </div>
        </Group>
        <Group gap={6}>
          {lastRefreshed && (
            <Badge variant="light" color="violet" size="xs">
              {lastRefreshed.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
            </Badge>
          )}
          <Tooltip label="Refresh insights" withArrow>
            <ActionIcon
              variant="light" color="violet" size="sm"
              onClick={fetchInsights} loading={loading}
            >
              <IconRefresh size={14} />
            </ActionIcon>
          </Tooltip>
        </Group>
      </Group>

      <Divider mb="md" color="violet.2" />

      {/* Content */}
      {loading ? (
        <Center py="lg">
          <Stack align="center" gap={8}>
            <Loader color="violet" size="sm" type="dots" />
            <Text size="xs" c="dimmed">Analyzing your health data…</Text>
          </Stack>
        </Center>
      ) : error ? (
        <Center py="lg">
          <Stack align="center" gap={8}>
            <IconAlertCircle size={32} className="text-gray-300" />
            <Text size="sm" c="dimmed">Could not fetch AI insights. Is the AI service running?</Text>
            <Button size="xs" variant="light" color="violet" onClick={fetchInsights}>
              Try Again
            </Button>
          </Stack>
        </Center>
      ) : insights ? (
        <Stack gap="xs">
          {insights.split("\n")
            .map((line) => line.trim())
            .filter((line) => line.length > 0)
            .map((line, i) => (
              <Group key={i} gap={8} align="flex-start">
                {!line.startsWith("•") && !line.match(/^[\p{Emoji}]/u) && (
                  <IconSparkles size={14} className="text-violet-400 mt-0.5 shrink-0" />
                )}
                <Text size="sm" style={{ lineHeight: 1.6 }}>{line}</Text>
              </Group>
            ))}
        </Stack>
      ) : (
        <Center py="lg">
          <Stack align="center" gap={8}>
            <IconHeartbeat size={32} className="text-gray-300" />
            <Text size="sm" c="dimmed">No insights yet.</Text>
          </Stack>
        </Center>
      )}
    </Card>
  );
};

export default AIHealthInsights;
