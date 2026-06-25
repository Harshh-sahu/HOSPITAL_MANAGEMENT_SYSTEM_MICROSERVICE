import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import {
  Card,
  Text,
  Loader,
  Center,
  Stack,
  Group,
  RingProgress,
  SimpleGrid,
  Badge,
} from "@mantine/core";
import {
  IconChartDonut,
  IconCalendarStats,
  IconStethoscope,
  IconTrendingUp,
} from "@tabler/icons-react";
import {
  countAppointmentByDoctor,
  countReasonByDoctor,
} from "../../../Service/AppointmentService";

interface MonthlyVisit { month: string; count: number; }
interface ReasonCount { reason: string; count: number; }

const COLORS = [
  "#4f86c6", "#f6a623", "#7ed957", "#e94f37",
  "#a78bfa", "#34d399", "#fb923c", "#60a5fa",
];

const DoctorStats = () => {
  const user = useSelector((state: any) => state.user);
  const [monthlyData, setMonthlyData] = useState<MonthlyVisit[]>([]);
  const [reasonData, setReasonData] = useState<ReasonCount[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const doctorId = user?.profileId;
    if (!doctorId) return;

    Promise.all([
      countAppointmentByDoctor(doctorId),
      countReasonByDoctor(doctorId),
    ])
      .then(([monthly, reasons]) => {
        setMonthlyData(Array.isArray(monthly) ? monthly : []);
        setReasonData(Array.isArray(reasons) ? reasons : []);
      })
      .catch((err) => console.error("Error fetching doctor stats:", err))
      .finally(() => setLoading(false));
  }, [user?.profileId]);

  if (loading) {
    return (
      <Center h={300}>
        <Loader color="primary" />
      </Center>
    );
  }

  const totalAppointments = monthlyData.reduce((sum, m) => sum + (m.count ?? 0), 0);
  const reasonTotal = reasonData.reduce((sum, r) => sum + (r.count ?? 0), 0) || 1;
  const sections = reasonData.map((r, i) => ({
    value: Math.round((r.count / reasonTotal) * 100),
    color: COLORS[i % COLORS.length],
    tooltip: `${r.reason}: ${r.count}`,
  }));

  return (
    <Stack gap="xl">
      {/* Total Appointments Card */}
      <Card withBorder radius="md" p="lg" shadow="xs">
        <Group gap="md">
          <div className="p-3 rounded-full bg-primary-50">
            <IconCalendarStats size={28} className="text-primary-500" />
          </div>
          <div>
            <Text size="xs" c="dimmed" tt="uppercase" fw={500}>
              Total Appointments
            </Text>
            <Text size="2rem" fw={700} className="text-primary-600">
              {totalAppointments}
            </Text>
          </div>
        </Group>
      </Card>

      {/* Monthly Breakdown */}
      {monthlyData.length > 0 && (
        <Card withBorder radius="md" p="lg" shadow="xs">
          <Group gap={6} mb="md">
            <IconTrendingUp size={18} className="text-primary-500" />
            <Text fw={600}>Monthly Appointments</Text>
          </Group>
          <SimpleGrid cols={{ base: 2, sm: 3, md: 4 }} spacing="sm">
            {monthlyData.map((m) => (
              <Card key={m.month} withBorder radius="sm" p="sm" bg="gray.0">
                <Text size="xs" c="dimmed" tt="uppercase" fw={500}>{m.month}</Text>
                <Text fw={700} size="lg" className="text-primary-600">{m.count}</Text>
              </Card>
            ))}
          </SimpleGrid>
        </Card>
      )}

      {/* Visit Reasons Breakdown */}
      <Card withBorder radius="md" p="lg" shadow="xs">
        <Group gap={6} mb="md">
          <IconChartDonut size={18} className="text-primary-500" />
          <Text fw={600}>Visit Reasons Breakdown</Text>
        </Group>

        {reasonData.length === 0 ? (
          <Center h={120}>
            <Text c="dimmed" size="sm">No visit reason data available.</Text>
          </Center>
        ) : (
          <Group justify="space-between" align="flex-start" wrap="wrap">
            <RingProgress
              size={180}
              thickness={22}
              sections={sections}
              label={
                <Center>
                  <Stack align="center" gap={2}>
                    <IconStethoscope size={20} className="text-gray-400" />
                    <Text size="xs" c="dimmed">{reasonData.length} reasons</Text>
                  </Stack>
                </Center>
              }
            />
            <SimpleGrid cols={1} style={{ flex: 1, minWidth: 160 }}>
              {reasonData.map((r, i) => (
                <Group key={r.reason} gap="sm" justify="space-between">
                  <Group gap={8}>
                    <div
                      style={{
                        width: 12, height: 12, borderRadius: 3,
                        background: COLORS[i % COLORS.length], flexShrink: 0,
                      }}
                    />
                    <Text size="sm">{r.reason}</Text>
                  </Group>
                  <Badge variant="light" color="gray" size="sm">{r.count}</Badge>
                </Group>
              ))}
            </SimpleGrid>
          </Group>
        )}
      </Card>
    </Stack>
  );
};

export default DoctorStats;
