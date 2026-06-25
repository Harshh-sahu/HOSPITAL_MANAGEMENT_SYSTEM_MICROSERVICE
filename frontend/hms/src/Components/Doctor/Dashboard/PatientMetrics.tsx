import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { SimpleGrid, Card, Text, Group, Skeleton } from "@mantine/core";
import {
  IconCalendarCheck,
  IconUsers,
  IconClipboardList,
  IconCalendarStar,
} from "@tabler/icons-react";
import { countAppointmentByDoctor, getAllPrescriptions, getTodaysAppointments } from "../../../Service/AppointmentService";
import { getAllPatient } from "../../../Service/PatientProfileService";

interface StatItem {
  label: string;
  value: number | null;
  icon: React.ReactNode;
  color: string;
  bg: string;
}

const StatCard = ({ item }: { item: StatItem }) => (
  <Card withBorder radius="md" p="md" className={item.bg} shadow="xs">
    <Group justify="space-between">
      <div>
        <Text size="xs" c="dimmed" fw={500} tt="uppercase">{item.label}</Text>
        {item.value === null ? (
          <Skeleton height={28} width={60} mt={4} />
        ) : (
          <Text size="1.6rem" fw={700} className={item.color}>{item.value}</Text>
        )}
      </div>
      <div className={`p-2 rounded-xl ${item.bg}`}>{item.icon}</div>
    </Group>
  </Card>
);

const PatientMetrics = () => {
  const user = useSelector((state: any) => state.user);
  const [totalPatients, setTotalPatients] = useState<number | null>(null);
  const [totalAppointments, setTotalAppointments] = useState<number | null>(null);
  const [todayCount, setTodayCount] = useState<number | null>(null);
  const [prescriptionCount, setPrescriptionCount] = useState<number | null>(null);

  useEffect(() => {
    getAllPatient()
      .then((res: any[]) => setTotalPatients(res.length))
      .catch(() => setTotalPatients(0));

    countAppointmentByDoctor(user?.profileId)
      .then((res: any[]) => {
        const total = res.reduce((sum: number, m: any) => sum + (m.count ?? 0), 0);
        setTotalAppointments(total);
      })
      .catch(() => setTotalAppointments(0));

    getTodaysAppointments()
      .then((res: any[]) => setTodayCount(res.length))
      .catch(() => setTodayCount(0));

    getAllPrescriptions()
      .then((res: any[]) => setPrescriptionCount(res.length))
      .catch(() => setPrescriptionCount(0));
  }, [user?.profileId]);

  const stats: StatItem[] = [
    {
      label: "Total Patients",
      value: totalPatients,
      icon: <IconUsers size={22} className="text-orange-500" />,
      color: "text-orange-600",
      bg: "bg-orange-50",
    },
    {
      label: "Total Appointments",
      value: totalAppointments,
      icon: <IconCalendarCheck size={22} className="text-violet-500" />,
      color: "text-violet-600",
      bg: "bg-violet-50",
    },
    {
      label: "Today's Schedule",
      value: todayCount,
      icon: <IconCalendarStar size={22} className="text-blue-500" />,
      color: "text-blue-600",
      bg: "bg-blue-50",
    },
    {
      label: "Prescriptions Issued",
      value: prescriptionCount,
      icon: <IconClipboardList size={22} className="text-green-500" />,
      color: "text-green-600",
      bg: "bg-green-50",
    },
  ];

  return (
    <div className="rounded-xl border bg-white p-4 shadow-sm">
      <div className="font-semibold text-lg mb-3">Quick Overview</div>
      <SimpleGrid cols={{ base: 2, sm: 2, md: 4 }} spacing="sm">
        {stats.map((s) => <StatCard key={s.label} item={s} />)}
      </SimpleGrid>
    </div>
  );
};

export default PatientMetrics;
