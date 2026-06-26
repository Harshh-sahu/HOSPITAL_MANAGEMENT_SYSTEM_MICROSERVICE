import { useEffect, useState } from "react";
import {
  Stack, Card, Text, Group, Loader, Center,
  SimpleGrid, Badge, Divider,
} from "@mantine/core";
import { AreaChart, BarChart, DonutChart, LineChart } from "@mantine/charts";
import {
  IconCalendarStats, IconTrendingUp, IconChartBar,
  IconStethoscope, IconUsers, IconChartDonut,
} from "@tabler/icons-react";
import {
  countAllAppointments, countAllReasons, getAllPrescriptions,
} from "../../../Service/AppointmentService";
import { getAllPatient } from "../../../Service/PatientProfileService";
import { getAllDoctor } from "../../../Service/DoctorProfileService";
import { getAllsales } from "../../../Service/SalesService";
import { addZeroMonths, convertReasonChartData } from "../../../Utility/OtherUtility";

const AdminStatsComponent = () => {
  const [monthlyAppts, setMonthlyAppts] = useState<any[]>([]);
  const [reasonData, setReasonData] = useState<any[]>([]);
  const [prescriptions, setPrescriptions] = useState<any[]>([]);
  const [patientCount, setPatientCount] = useState<number>(0);
  const [doctorCount, setDoctorCount] = useState<number>(0);
  const [sales, setSales] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.allSettled([
      countAllAppointments(),
      countAllReasons(),
      getAllPrescriptions(),
      getAllPatient(),
      getAllDoctor(),
      getAllsales(),
    ]).then(([appts, reasons, presc, patients, doctors, salesRes]) => {
      setMonthlyAppts(appts.status === "fulfilled" ? addZeroMonths(appts.value, "month", "count") : []);
      setReasonData(reasons.status === "fulfilled" ? convertReasonChartData(reasons.value) : []);
      setPrescriptions(presc.status === "fulfilled" ? presc.value : []);
      setPatientCount(patients.status === "fulfilled" ? patients.value.length : 0);
      setDoctorCount(doctors.status === "fulfilled" ? doctors.value.length : 0);
      setSales(salesRes.status === "fulfilled" ? salesRes.value : []);
    }).finally(() => setLoading(false));
  }, []);

  if (loading) return <Center h={400}><Loader color="blue" size="lg" /></Center>;

  const totalAppts = monthlyAppts.reduce((s, m) => s + (m.count ?? 0), 0);
  const totalRevenue = sales.reduce((s: number, sale: any) => s + (sale.totalAmount ?? sale.amount ?? 0), 0);

  // Monthly sales chart
  const salesByMonth: Record<string, number> = {};
  sales.forEach((s: any) => {
    const d = new Date(s.saleDate ?? s.createdAt ?? Date.now());
    const label = d.toLocaleString("default", { month: "short" });
    salesByMonth[label] = (salesByMonth[label] ?? 0) + (s.totalAmount ?? s.amount ?? 1);
  });
  const salesChartData = Object.entries(salesByMonth).map(([month, total]) => ({ month, total }));

  // Medicine frequency from prescriptions
  const medFreq: Record<string, number> = {};
  prescriptions.forEach((p: any) => {
    (p.medicines ?? []).forEach((m: any) => {
      const name = m.medicineName ?? m.name ?? "Unknown";
      medFreq[name] = (medFreq[name] ?? 0) + 1;
    });
  });
  const topMeds = Object.entries(medFreq).sort((a, b) => b[1] - a[1]).slice(0, 8)
    .map(([name, count]) => ({ name, count }));

  return (
    <Stack gap="xl">

      {/* Summary row */}
      <SimpleGrid cols={{ base: 2, sm: 4 }} spacing="md">
        {[
          { label: "Total Patients", value: patientCount, color: "text-green-600", bg: "bg-green-50" },
          { label: "Total Doctors", value: doctorCount, color: "text-blue-600", bg: "bg-blue-50" },
          { label: "Total Appointments", value: totalAppts, color: "text-violet-600", bg: "bg-violet-50" },
          { label: "Total Revenue", value: `₹${totalRevenue.toLocaleString("en-IN")}`, color: "text-teal-600", bg: "bg-teal-50" },
        ].map((s) => (
          <Card key={s.label} withBorder radius="md" p="md" shadow="xs" className={s.bg}>
            <Text size="xs" c="dimmed" fw={500} tt="uppercase">{s.label}</Text>
            <Text size="1.6rem" fw={700} className={s.color}>{s.value}</Text>
          </Card>
        ))}
      </SimpleGrid>

      {/* Monthly appointment trend */}
      <Card withBorder radius="md" p="lg" shadow="xs">
        <Group gap={6} mb="md" justify="space-between">
          <Group gap={6}>
            <IconTrendingUp size={18} className="text-primary-500" />
            <Text fw={600}>Monthly Appointment Trend</Text>
          </Group>
          <Badge variant="light" color="blue">{new Date().getFullYear()}</Badge>
        </Group>
        {monthlyAppts.every(m => m.count === 0) ? (
          <Center h={120}><Text c="dimmed" size="sm">No appointment data this year.</Text></Center>
        ) : (
          <AreaChart h={200} data={monthlyAppts} dataKey="month"
            series={[{ name: "count", color: "blue", label: "Appointments" }]}
            curveType="bump" withGradient fillOpacity={0.6} strokeWidth={3}
            tickLine="none" gridAxis="xy" withDots />
        )}
      </Card>

      {/* Reasons + Top medicines */}
      <SimpleGrid cols={{ base: 1, md: 2 }} spacing="md">
        <Card withBorder radius="md" p="lg" shadow="xs">
          <Group gap={6} mb="md">
            <IconChartDonut size={18} className="text-primary-500" />
            <Text fw={600}>Visit Reasons Distribution</Text>
          </Group>
          {reasonData.length === 0 ? (
            <Center h={160}><Text c="dimmed" size="sm">No data available.</Text></Center>
          ) : (
            <Center>
              <DonutChart data={reasonData} withLabels withLabelsLine
                labelsType="percent" chartLabel="Reasons"
                thickness={25} size={200} paddingAngle={5} />
            </Center>
          )}
        </Card>

        <Card withBorder radius="md" p="lg" shadow="xs">
          <Group gap={6} mb="md">
            <IconChartBar size={18} className="text-primary-500" />
            <Text fw={600}>Visit Reasons Count</Text>
          </Group>
          {reasonData.length === 0 ? (
            <Center h={160}><Text c="dimmed" size="sm">No data available.</Text></Center>
          ) : (
            <BarChart h={200}
              data={reasonData.map(r => ({ reason: r.name, count: r.value }))}
              dataKey="reason"
              series={[{ name: "count", color: "green.5", label: "Appointments" }]}
              tickLine="none" gridAxis="y" withBarValueLabel barProps={{ radius: 4 }} />
          )}
        </Card>
      </SimpleGrid>

      {/* Sales trend */}
      <Card withBorder radius="md" p="lg" shadow="xs">
        <Group gap={6} mb="md" justify="space-between">
          <Group gap={6}>
            <IconCalendarStats size={18} className="text-primary-500" />
            <Text fw={600}>Monthly Sales Revenue</Text>
          </Group>
          <Badge variant="light" color="teal">₹{totalRevenue.toLocaleString("en-IN")} total</Badge>
        </Group>
        {salesChartData.length === 0 ? (
          <Center h={120}><Text c="dimmed" size="sm">No sales data available.</Text></Center>
        ) : (
          <BarChart h={180} data={salesChartData} dataKey="month"
            series={[{ name: "total", color: "teal.5", label: "Revenue (₹)" }]}
            tickLine="none" gridAxis="y" withBarValueLabel barProps={{ radius: 4 }} />
        )}
      </Card>

      {/* Top prescribed medicines */}
      {topMeds.length > 0 && (
        <Card withBorder radius="md" p="lg" shadow="xs">
          <Group gap={6} mb="md">
            <IconStethoscope size={18} className="text-primary-500" />
            <Text fw={600}>Top Prescribed Medicines</Text>
            <Badge variant="light" color="gray" size="sm" ml="auto">Top {topMeds.length}</Badge>
          </Group>
          <BarChart h={200} data={topMeds} dataKey="name"
            series={[{ name: "count", color: "orange.5", label: "Times Prescribed" }]}
            tickLine="none" gridAxis="y" withBarValueLabel barProps={{ radius: 4 }} />
        </Card>
      )}

      {/* Patient vs Doctor ratio */}
      <Card withBorder radius="md" p="lg" shadow="xs">
        <Group gap={6} mb="md">
          <IconUsers size={18} className="text-primary-500" />
          <Text fw={600}>Hospital Composition</Text>
        </Group>
        <SimpleGrid cols={2} spacing="md">
          <div className="text-center p-4 bg-green-50 rounded-xl">
            <Text size="2.5rem" fw={800} c="green.6">{patientCount}</Text>
            <Text size="sm" c="dimmed">Total Patients</Text>
          </div>
          <div className="text-center p-4 bg-blue-50 rounded-xl">
            <Text size="2.5rem" fw={800} c="blue.6">{doctorCount}</Text>
            <Text size="sm" c="dimmed">Total Doctors</Text>
          </div>
        </SimpleGrid>
        {patientCount > 0 && doctorCount > 0 && (
          <Text size="xs" c="dimmed" mt="sm" ta="center">
            Patient-to-Doctor ratio: {(patientCount / doctorCount).toFixed(1)}:1
          </Text>
        )}
      </Card>

    </Stack>
  );
};

export default AdminStatsComponent;
