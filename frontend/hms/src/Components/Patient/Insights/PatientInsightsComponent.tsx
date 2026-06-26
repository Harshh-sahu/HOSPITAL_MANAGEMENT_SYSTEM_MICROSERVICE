import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import {
  Stack, Card, Text, Group, Loader, Center, Button, Badge,
  Divider, ThemeIcon, SimpleGrid, ScrollArea, ActionIcon, Tooltip,
} from "@mantine/core";
import { AreaChart, BarChart, DonutChart } from "@mantine/charts";
import {
  IconBrain, IconRefresh, IconSparkles, IconAlertCircle,
  IconCalendarStats, IconPill, IconFileReport, IconHeartbeat,
  IconActivity, IconTrendingUp, IconChartBar,
} from "@tabler/icons-react";
import {
  countAppointmentByPatient,
  countReasonByPatient,
  getReportByPatientId,
  getMedicineConsumeByPatient,
  getPrescriptionByPatientId,
} from "../../../Service/AppointmentService";
import { sendChatMessage } from "../../../Service/ChatService";
import { addZeroMonths, convertReasonChartData } from "../../../Utility/OtherUtility";

const INSIGHTS_CONV_KEY = "hms_ai_insights_full_id";

function getConvId(): string {
  let id = localStorage.getItem(INSIGHTS_CONV_KEY);
  if (!id) {
    id = `insights-full-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`;
    localStorage.setItem(INSIGHTS_CONV_KEY, id);
  }
  return id;
}

const PatientInsightsComponent = () => {
  const user = useSelector((state: any) => state.user);

  const [monthlyData, setMonthlyData] = useState<any[]>([]);
  const [reasonData, setReasonData] = useState<any[]>([]);
  const [records, setRecords] = useState<any[]>([]);
  const [medicines, setMedicines] = useState<any[]>([]);
  const [prescriptions, setPrescriptions] = useState<any[]>([]);

  const [insights, setInsights] = useState<string | null>(null);
  const [aiLoading, setAiLoading] = useState(false);
  const [aiError, setAiError] = useState(false);
  const [dataLoading, setDataLoading] = useState(true);
  const [lastRefreshed, setLastRefreshed] = useState<Date | null>(null);

  const loadData = async () => {
    const id = user?.profileId;
    if (!id) return;
    setDataLoading(true);
    try {
      const [monthly, reasons, recs, meds, presc] = await Promise.allSettled([
        countAppointmentByPatient(id),
        countReasonByPatient(id),
        getReportByPatientId(id),
        getMedicineConsumeByPatient(id),
        getPrescriptionByPatientId(id),
      ]);

      const monthlyArr = monthly.status === "fulfilled"
        ? addZeroMonths(monthly.value, "month", "count") : [];
      setMonthlyData(monthlyArr);
      setReasonData(reasons.status === "fulfilled" ? convertReasonChartData(reasons.value) : []);
      setRecords(recs.status === "fulfilled" ? recs.value : []);
      setMedicines(meds.status === "fulfilled" ? meds.value : []);
      setPrescriptions(presc.status === "fulfilled" ? presc.value : []);
    } finally {
      setDataLoading(false);
    }
  };

  const fetchAIInsights = async () => {
    const id = user?.profileId;
    if (!id) return;
    setAiLoading(true);
    setAiError(false);

    const totalVisits = monthlyData.reduce((s, m) => s + (m.count ?? 0), 0);
    const diagnoses = records.map((r: any) => r.diagnosis).filter(Boolean);
    const medNames = medicines.map((m: any) => m.medicineName ?? m.name).filter(Boolean);

    const prompt = `You are a helpful health assistant for a hospital app. Based on this patient's health summary, provide a detailed health analysis with:
1. Overall health assessment (2-3 sentences)
2. Key health observations (3-4 bullet points with emojis)
3. Personalized wellness recommendations (3-4 bullet points with emojis)
4. A short reminder about when to see a doctor

Patient: ${user?.name}
Total hospital visits: ${totalVisits}
Medical diagnoses on record: ${diagnoses.join(", ") || "none recorded"}
Current/past medicines: ${medNames.slice(0, 8).join(", ") || "none recorded"}
Number of prescriptions: ${prescriptions.length}

Be warm, encouraging and specific to their data. Keep total response under 300 words.`;

    try {
      const result = await sendChatMessage({
        message: prompt,
        conversationId: getConvId(),
        role: user?.role ?? "PATIENT",
        userName: user?.name,
        profileId: user?.profileId,
      });
      setInsights(result.response);
      setLastRefreshed(new Date());
    } catch {
      setAiError(true);
    } finally {
      setAiLoading(false);
    }
  };

  useEffect(() => { loadData(); }, [user?.profileId]);
  useEffect(() => {
    if (!dataLoading) fetchAIInsights();
  }, [dataLoading]);

  const totalVisits = monthlyData.reduce((s, m) => s + (m.count ?? 0), 0);

  const medFreq: Record<string, number> = {};
  medicines.forEach((m: any) => {
    const name = m.medicineName ?? m.name ?? "Unknown";
    medFreq[name] = (medFreq[name] ?? 0) + 1;
  });
  const topMeds = Object.entries(medFreq)
    .sort((a, b) => b[1] - a[1]).slice(0, 6)
    .map(([name, count]) => ({ name, count }));

  if (dataLoading) {
    return <Center h={400}><Loader color="violet" size="lg" /></Center>;
  }

  return (
    <Stack gap="xl">

      {/* Summary Cards */}
      <SimpleGrid cols={{ base: 2, sm: 4 }} spacing="md">
        {[
          { label: "Total Visits", value: totalVisits, icon: <IconCalendarStats size={20} />, color: "text-violet-600", bg: "bg-violet-50" },
          { label: "Medical Records", value: records.length, icon: <IconFileReport size={20} />, color: "text-blue-600", bg: "bg-blue-50" },
          { label: "Prescriptions", value: prescriptions.length, icon: <IconPill size={20} />, color: "text-green-600", bg: "bg-green-50" },
          { label: "Medicines", value: medicines.length, icon: <IconActivity size={20} />, color: "text-orange-600", bg: "bg-orange-50" },
        ].map((s) => (
          <Card key={s.label} withBorder radius="md" p="md" shadow="xs" className={s.bg}>
            <Group justify="space-between">
              <div>
                <Text size="xs" c="dimmed" fw={500} tt="uppercase">{s.label}</Text>
                <Text size="1.6rem" fw={700} className={s.color}>{s.value}</Text>
              </div>
              <div className={`p-2 rounded-xl ${s.bg} text-gray-500`}>{s.icon}</div>
            </Group>
          </Card>
        ))}
      </SimpleGrid>

      {/* AI Health Analysis */}
      <Card withBorder radius="md" p="lg" shadow="xs"
        className="bg-gradient-to-br from-indigo-50 to-purple-50"
        style={{ borderColor: "#c4b5fd" }}>
        <Group justify="space-between" mb="md">
          <Group gap={10}>
            <ThemeIcon size="lg" radius="xl" variant="gradient" gradient={{ from: "indigo", to: "violet" }}>
              <IconBrain size={18} />
            </ThemeIcon>
            <div>
              <Text fw={700}>AI Health Analysis</Text>
              <Text size="xs" c="dimmed">Personalized insights based on your health data</Text>
            </div>
          </Group>
          <Group gap={6}>
            {lastRefreshed && (
              <Badge variant="light" color="violet" size="xs">
                Updated {lastRefreshed.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
              </Badge>
            )}
            <Tooltip label="Refresh AI analysis" withArrow>
              <ActionIcon variant="light" color="violet" onClick={fetchAIInsights} loading={aiLoading}>
                <IconRefresh size={16} />
              </ActionIcon>
            </Tooltip>
          </Group>
        </Group>
        <Divider mb="md" color="violet.2" />
        {aiLoading ? (
          <Center py="xl">
            <Stack align="center" gap={8}>
              <Loader color="violet" type="dots" />
              <Text size="sm" c="dimmed">AI is analyzing your health data…</Text>
            </Stack>
          </Center>
        ) : aiError ? (
          <Center py="xl">
            <Stack align="center" gap={8}>
              <IconAlertCircle size={36} className="text-gray-300" />
              <Text c="dimmed" size="sm">AI service unavailable. Please try again.</Text>
              <Button size="xs" variant="light" color="violet" onClick={fetchAIInsights}>Retry</Button>
            </Stack>
          </Center>
        ) : insights ? (
          <ScrollArea mah={320}>
            <Stack gap="xs">
              {insights.split("\n").map(l => l.trim()).filter(l => l.length > 0).map((line, i) => (
                <Group key={i} gap={8} align="flex-start">
                  {!line.match(/^[\p{Emoji}•\-*\d]/u) && (
                    <IconSparkles size={14} className="text-violet-400 mt-0.5 shrink-0" />
                  )}
                  <Text size="sm" style={{ lineHeight: 1.7 }}>{line}</Text>
                </Group>
              ))}
            </Stack>
          </ScrollArea>
        ) : (
          <Center py="xl">
            <Button size="xs" variant="light" color="violet" onClick={fetchAIInsights}>Generate Insights</Button>
          </Center>
        )}
      </Card>

      {/* Monthly Visits Trend */}
      <Card withBorder radius="md" p="lg" shadow="xs">
        <Group gap={6} mb="md">
          <IconTrendingUp size={18} className="text-primary-500" />
          <Text fw={600}>Monthly Visit Trend</Text>
          <Badge variant="light" color="violet" size="sm" ml="auto">{new Date().getFullYear()}</Badge>
        </Group>
        {monthlyData.every(m => m.count === 0) ? (
          <Center h={120}><Text c="dimmed" size="sm">No visit data this year.</Text></Center>
        ) : (
          <AreaChart
            h={200} data={monthlyData} dataKey="month"
            series={[{ name: "count", color: "violet", label: "Visits" }]}
            curveType="bump" withGradient fillOpacity={0.6}
            strokeWidth={3} tickLine="none" gridAxis="xy" withDots
          />
        )}
      </Card>

      {/* Visit Reasons + Top Medicines */}
      <SimpleGrid cols={{ base: 1, md: 2 }} spacing="md">
        <Card withBorder radius="md" p="lg" shadow="xs">
          <Group gap={6} mb="md">
            <IconChartBar size={18} className="text-primary-500" />
            <Text fw={600}>Visit Reasons</Text>
          </Group>
          {reasonData.length === 0 ? (
            <Center h={160}><Text c="dimmed" size="sm">No reason data available.</Text></Center>
          ) : (
            <Center>
              <DonutChart
                data={reasonData} withLabels withLabelsLine
                labelsType="percent" chartLabel="Reasons"
                thickness={25} size={200} paddingAngle={5}
              />
            </Center>
          )}
        </Card>

        <Card withBorder radius="md" p="lg" shadow="xs">
          <Group gap={6} mb="md">
            <IconPill size={18} className="text-primary-500" />
            <Text fw={600}>Top Medicines</Text>
          </Group>
          {topMeds.length === 0 ? (
            <Center h={160}><Text c="dimmed" size="sm">No medicine data available.</Text></Center>
          ) : (
            <BarChart
              h={200} data={topMeds} dataKey="name"
              series={[{ name: "count", color: "orange.5", label: "Times Prescribed" }]}
              tickLine="none" gridAxis="y" withBarValueLabel barProps={{ radius: 4 }}
            />
          )}
        </Card>
      </SimpleGrid>

      {/* Recent Medical Records */}
      {records.length > 0 && (
        <Card withBorder radius="md" p="lg" shadow="xs">
          <Group gap={6} mb="md">
            <IconFileReport size={18} className="text-primary-500" />
            <Text fw={600}>Recent Medical Records</Text>
            <Badge variant="light" color="gray" size="sm" ml="auto">{records.length} total</Badge>
          </Group>
          <Stack gap="xs">
            {records.slice(0, 5).map((r: any, i: number) => (
              <div key={r.id ?? i}>
                <Group justify="space-between" py="xs">
                  <div>
                    <Text size="sm" fw={600}>{r.diagnosis ?? "Diagnosis not recorded"}</Text>
                    <Text size="xs" c="dimmed">{r.doctorName ?? ""}</Text>
                  </div>
                  <Group gap={6}>
                    {r.symptoms?.slice(0, 2).map((s: string) => (
                      <Badge key={s} size="xs" variant="light" color="blue">{s}</Badge>
                    ))}
                    <Text size="xs" c="dimmed">
                      {r.createdAt ? new Date(r.createdAt).toLocaleDateString("en-IN", { day: "numeric", month: "short" }) : ""}
                    </Text>
                  </Group>
                </Group>
                {i < Math.min(records.length, 5) - 1 && <Divider />}
              </div>
            ))}
          </Stack>
        </Card>
      )}

    </Stack>
  );
};

export default PatientInsightsComponent;
