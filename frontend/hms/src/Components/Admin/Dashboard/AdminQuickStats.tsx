import { useEffect, useState } from "react";
import { Card, Group, SimpleGrid, Skeleton, Text } from "@mantine/core";
import {
  IconCalendarCheck, IconCalendarStar,
  IconClipboardList, IconUsers, IconStethoscope,
} from "@tabler/icons-react";
import {
  countAllAppointments, getTodaysAppointments, getAllPrescriptions,
} from "../../../Service/AppointmentService";
import { getAllPatient } from "../../../Service/PatientProfileService";
import { getAllDoctor } from "../../../Service/DoctorProfileService";

const AdminQuickStats = () => {
  const [totalAppts, setTotalAppts] = useState<number | null>(null);
  const [totalPatients, setTotalPatients] = useState<number | null>(null);
  const [totalDoctors, setTotalDoctors] = useState<number | null>(null);
  const [todayAppts, setTodayAppts] = useState<number | null>(null);
  const [totalPrescriptions, setTotalPrescriptions] = useState<number | null>(null);

  useEffect(() => {
    countAllAppointments()
      .then((res: any[]) => setTotalAppts(res.reduce((s, m) => s + (m.count ?? 0), 0)))
      .catch(() => setTotalAppts(0));
    getAllPatient().then((res: any[]) => setTotalPatients(res.length)).catch(() => setTotalPatients(0));
    getAllDoctor().then((res: any[]) => setTotalDoctors(res.length)).catch(() => setTotalDoctors(0));
    getTodaysAppointments().then((res: any[]) => setTodayAppts(res.length)).catch(() => setTodayAppts(0));
    getAllPrescriptions().then((res: any[]) => setTotalPrescriptions(res.length)).catch(() => setTotalPrescriptions(0));
  }, []);

  const stats = [
    { label: "Total Patients", value: totalPatients, icon: <IconUsers size={22} className="text-green-500" />, color: "text-green-600", bg: "bg-green-50" },
    { label: "Total Doctors", value: totalDoctors, icon: <IconStethoscope size={22} className="text-blue-500" />, color: "text-blue-600", bg: "bg-blue-50" },
    { label: "Total Appointments", value: totalAppts, icon: <IconCalendarCheck size={22} className="text-violet-500" />, color: "text-violet-600", bg: "bg-violet-50" },
    { label: "Today's Appointments", value: todayAppts, icon: <IconCalendarStar size={22} className="text-orange-500" />, color: "text-orange-600", bg: "bg-orange-50" },
    { label: "Total Prescriptions", value: totalPrescriptions, icon: <IconClipboardList size={22} className="text-teal-500" />, color: "text-teal-600", bg: "bg-teal-50" },
  ];

  return (
    <SimpleGrid cols={{ base: 2, sm: 3, md: 5 }} spacing="md">
      {stats.map((s) => (
        <Card key={s.label} withBorder radius="md" p="md" shadow="xs" className={s.bg}>
          <Group justify="space-between" align="flex-start">
            <div>
              <Text size="xs" c="dimmed" fw={500} tt="uppercase">{s.label}</Text>
              {s.value === null
                ? <Skeleton height={28} width={50} mt={4} />
                : <Text size="1.6rem" fw={700} className={s.color}>{s.value}</Text>}
            </div>
            <div className={`p-2 rounded-xl ${s.bg}`}>{s.icon}</div>
          </Group>
        </Card>
      ))}
    </SimpleGrid>
  );
};

export default AdminQuickStats;
