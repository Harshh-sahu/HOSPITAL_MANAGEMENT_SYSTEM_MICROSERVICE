import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import {
  Card, Text, Loader, Center, Stack, Group,
  SimpleGrid, Badge, Avatar, Divider, ScrollArea,
} from "@mantine/core";
import { AreaChart, BarChart, DonutChart } from "@mantine/charts";
import {
  IconCalendarStats, IconUsers, IconClipboardList,
  IconTrendingUp, IconChartBar, IconPill,
  IconCalendarStar, IconStethoscope,
} from "@tabler/icons-react";
import {
  countAppointmentByDoctor,
  countReasonByDoctor,
  getAllPrescriptions,
  getTodaysAppointments,
} from "../../../Service/AppointmentService";
import { getAllPatient } from "../../../Service/PatientProfileService";
import { addZeroMonths } from "../../../Utility/OtherUtility";

interface MonthlyVisit { month: string; count: number; }
interface ReasonCount { reason: string; count: number; }

const DONUT_COLORS = ["red.6", "blue.6", "teal.6", "orange.6", "violet.6", "green.6", "yellow.6", "cyan.6"];
const BAR_COLORS = ["#4f86c6", "#f6a623", "#7ed957", "#e94f37", "#a78bfa", "#34d399", "#fb923c", "#60a5fa"];

// ─── Summary Stat Card ────────────────────────────────────────────
const StatCard = ({ label, value, icon, color, bg }: any) => (
  <Card withBorder radius="md" p="md" shadow="xs" className={bg}>
    <Group justify="space-between" align="flex-start">
      <div>
        <Text size="xs" c="dimmed" fw={500} tt="uppercase">{label}</Text>
        {value === null ? (
          <Text size="1.8rem" fw={700} c="dimmed">—</Text>
        ) : (
          <Text size="1.8rem" fw={700} className={color}>{value}</Text>
        )}
      </div>
      <div className={`p-2 rounded-xl ${bg}`}>{icon}</div>
    </Group>
  </Card>
);

// ─── Main Component ───────────────────────────────────────────────
const DoctorStats = () => {
  const user = useSelector((state: any) => state.user);

  const [monthlyData, setMonthlyData] = useState<MonthlyVisit[]>([]);
  const [reasonData, setReasonData] = useState<ReasonCount[]>([]);
  const [prescriptions, setPrescriptions] = useState<any[]>([]);
  const [totalPatients, setTotalPatients] = useState<number | null>(null);
  const [todayCount, setTodayCount] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const doctorId = user?.profileId;
    if (!doctorId) return;

    Promise.all([
      countAppointmentByDoctor(doctorId),
      countReasonByDoctor(doctorId),
      getAllPrescriptions(),
      getAllPatient(),
      getTodaysAppointments(),
    ])
      .then(([monthly, reasons, prescriptions, patients, today]) => {
        setMonthlyData(addZeroMonths(Array.isArray(monthly) ? monthly : [], "month", "count"));
        setReasonData(Array.isArray(reasons) ? reasons : []);
        setPrescriptions(Array.isArray(prescriptions) ? prescriptions : []);
        setTotalPatients(Array.isArray(patients) ? patients.length : 0);
        setTodayCount(Array.isArray(today) ? today.length : 0);
      })
      .catch((err) => console.error("Error fetching stats:", err))
      .finally(() => setLoading(false));
  }, [user?.profileId]);

  if (loading) {
    return (
      <Center h={400}>
        <Loader color="primary" size="lg" />
      </Center>
    );
  }

  const totalAppointments = monthlyData.reduce((s, m) => s + (m.count ?? 0), 0);
  const peakMonth = monthlyData.length
    ? monthlyData.reduce((a, b) => (a.count > b.count ? a : b))
    : null;

  // Build donut data for reasons
  const donutReasons = reasonData.map((r, i) => ({
    name: r.reason,
    value: r.count,
    color: DONUT_COLORS[i % DONUT_COLORS.length],
  }));

  // Compute top prescribed medicines from prescriptions
  const medicineFreq: Record<string, number> = {};
  prescriptions.forEach((p: any) => {
    (p.medicines ?? []).forEach((m: any) => {
      const name = m.medicineName ?? m.name ?? "Unknown";
      medicineFreq[name] = (medicineFreq[name] ?? 0) + 1;
    });
  });
  const topMedicines = Object.entries(medicineFreq)
    .sort((a, b) => b[1] - a[1])
    .slice(0, 6)
    .map(([name, count], i) => ({ name, count, color: BAR_COLORS[i % BAR_COLORS.length] }));

  // Recent prescriptions — latest 5
  const recentPrescriptions = [...prescriptions]
    .sort((a, b) => new Date(b.prescriptionDate ?? 0).getTime() - new Date(a.prescriptionDate ?? 0).getTime())
    .slice(0, 5);

  return (
    <Stack gap="xl">

      {/* ── 1. Summary stat cards ── */}
      <SimpleGrid cols={{ base: 2, sm: 2, md: 4 }} spacing="md">
        <StatCard
          label="Total Appointments"
          value={totalAppointments}
          icon={<IconCalendarStats size={22} className="text-violet-500" />}
          color="text-violet-600" bg="bg-violet-50"
        />
        <StatCard
          label="Total Patients"
          value={totalPatients}
          icon={<IconUsers size={22} className="text-orange-500" />}
          color="text-orange-600" bg="bg-orange-50"
        />
        <StatCard
          label="Prescriptions Written"
          value={prescriptions.length}
          icon={<IconClipboardList size={22} className="text-green-500" />}
          color="text-green-600" bg="bg-green-50"
        />
        <StatCard
          label="Today's Appointments"
          value={todayCount}
          icon={<IconCalendarStar size={22} className="text-blue-500" />}
          color="text-blue-600" bg="bg-blue-50"
        />
      </SimpleGrid>

      {/* ── 2. Monthly trend area chart ── */}
      <Card withBorder radius="md" p="lg" shadow="xs">
        <Group justify="space-between" mb="md">
          <Group gap={6}>
            <IconTrendingUp size={18} className="text-primary-500" />
            <Text fw={600}>Monthly Appointment Trend</Text>
          </Group>
          <Badge variant="light" color="primary">{new Date().getFullYear()}</Badge>
        </Group>
        {monthlyData.every(m => m.count === 0) ? (
          <Center h={120}><Text c="dimmed" size="sm">No appointment data for this year.</Text></Center>
        ) : (
          <AreaChart
            h={200}
            data={monthlyData}
            dataKey="month"
            series={[{ name: "count", color: "violet", label: "Appointments" }]}
            curveType="bump"
            withGradient
            fillOpacity={0.6}
            strokeWidth={3}
            tickLine="none"
            gridAxis="xy"
            withDots
            withLegend={false}
          />
        )}
      </Card>

      {/* ── 3. Visit reasons side by side (DonutChart + BarChart) ── */}
      <SimpleGrid cols={{ base: 1, md: 2 }} spacing="md">

        {/* Donut */}
        <Card withBorder radius="md" p="lg" shadow="xs">
          <Group gap={6} mb="md">
            <IconStethoscope size={18} className="text-primary-500" />
            <Text fw={600}>Visit Reasons</Text>
          </Group>
          {donutReasons.length === 0 ? (
            <Center h={160}><Text c="dimmed" size="sm">No data available.</Text></Center>
          ) : (
            <Center>
              <DonutChart
                data={donutReasons}
                withLabels
                withLabelsLine
                labelsType="percent"
                chartLabel="Reasons"
                thickness={25}
                size={200}
                paddingAngle={5}
              />
            </Center>
          )}
        </Card>

        {/* Bar chart */}
        <Card withBorder radius="md" p="lg" shadow="xs">
          <Group gap={6} mb="md">
            <IconChartBar size={18} className="text-primary-500" />
            <Text fw={600}>Reasons Count</Text>
          </Group>
          {reasonData.length === 0 ? (
            <Center h={160}><Text c="dimmed" size="sm">No data available.</Text></Center>
          ) : (
            <BarChart
              h={200}
              data={reasonData}
              dataKey="reason"
              series={[{ name: "count", color: "blue.5", label: "Appointments" }]}
              tickLine="none"
              gridAxis="y"
              withBarValueLabel
              barProps={{ radius: 4 }}
            />
          )}
        </Card>
      </SimpleGrid>

      {/* ── 4. Top prescribed medicines ── */}
      {topMedicines.length > 0 && (
        <Card withBorder radius="md" p="lg" shadow="xs">
          <Group gap={6} mb="md">
            <IconPill size={18} className="text-primary-500" />
            <Text fw={600}>Top Prescribed Medicines</Text>
            <Badge variant="light" color="gray" size="sm" ml="auto">Top {topMedicines.length}</Badge>
          </Group>
          <BarChart
            h={200}
            data={topMedicines}
            dataKey="name"
            series={[{ name: "count", color: "teal.5", label: "Times Prescribed" }]}
            tickLine="none"
            gridAxis="y"
            withBarValueLabel
            barProps={{ radius: 4 }}
          />
        </Card>
      )}

      {/* ── 5. Recent prescriptions timeline ── */}
      {recentPrescriptions.length > 0 && (
        <Card withBorder radius="md" p="lg" shadow="xs">
          <Group gap={6} mb="md">
            <IconClipboardList size={18} className="text-primary-500" />
            <Text fw={600}>Recent Prescriptions</Text>
          </Group>
          <ScrollArea>
            <Stack gap="xs">
              {recentPrescriptions.map((p: any, i: number) => (
                <div key={p.id ?? i}>
                  <Group justify="space-between" py="xs">
                    <Group gap="sm">
                      <Avatar size="sm" color="primary" radius="xl">
                        {(p.patientName ?? "P").charAt(0).toUpperCase()}
                      </Avatar>
                      <div>
                        <Text size="sm" fw={600}>{p.patientName ?? `Patient #${p.patientId}`}</Text>
                        <Text size="xs" c="dimmed">
                          {(p.medicines ?? []).length} medicine{(p.medicines ?? []).length !== 1 ? "s" : ""}
                          {p.notes ? ` · ${p.notes}` : ""}
                        </Text>
                      </div>
                    </Group>
                    <Text size="xs" c="dimmed">
                      {p.prescriptionDate
                        ? new Date(p.prescriptionDate).toLocaleDateString("en-IN", { day: "numeric", month: "short", year: "numeric" })
                        : "—"}
                    </Text>
                  </Group>
                  {i < recentPrescriptions.length - 1 && <Divider />}
                </div>
              ))}
            </Stack>
          </ScrollArea>
        </Card>
      )}

    </Stack>
  );
};

export default DoctorStats;
