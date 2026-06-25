import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { Card, Group, SimpleGrid, Skeleton, Text } from "@mantine/core";
import {
  IconCalendarCheck,
  IconFileReport,
  IconPill,
  IconActivity,
} from "@tabler/icons-react";
import {
  countAppointmentByPatient,
  getReportByPatientId,
  getPrescriptionByPatientId,
  getMedicineConsumeByPatient,
} from "../../../Service/AppointmentService";

interface Stat { label: string; value: number | null; icon: React.ReactNode; color: string; bg: string; }

const HealthSummary = () => {
  const user = useSelector((state: any) => state.user);
  const [stats, setStats] = useState<Stat[]>([
    { label: "Total Visits", value: null, icon: <IconCalendarCheck size={22} className="text-violet-500" />, color: "text-violet-600", bg: "bg-violet-50" },
    { label: "Medical Records", value: null, icon: <IconFileReport size={22} className="text-blue-500" />, color: "text-blue-600", bg: "bg-blue-50" },
    { label: "Prescriptions", value: null, icon: <IconPill size={22} className="text-green-500" />, color: "text-green-600", bg: "bg-green-50" },
    { label: "Medicines Taken", value: null, icon: <IconActivity size={22} className="text-orange-500" />, color: "text-orange-600", bg: "bg-orange-50" },
  ]);

  useEffect(() => {
    const id = user?.profileId;
    if (!id) return;
    Promise.allSettled([
      countAppointmentByPatient(id),
      getReportByPatientId(id),
      getPrescriptionByPatientId(id),
      getMedicineConsumeByPatient(id),
    ]).then(([visits, records, prescriptions, medicines]) => {
      const get = (r: PromiseSettledResult<any>, fn: (v: any) => number) =>
        r.status === "fulfilled" ? fn(r.value) : 0;

      setStats((prev) => [
        { ...prev[0], value: get(visits, (v) => v.reduce((s: number, m: any) => s + (m.count ?? 0), 0)) },
        { ...prev[1], value: get(records, (v) => v.length) },
        { ...prev[2], value: get(prescriptions, (v) => v.length) },
        { ...prev[3], value: get(medicines, (v) => v.length) },
      ]);
    });
  }, [user?.profileId]);

  return (
    <SimpleGrid cols={{ base: 2, sm: 4 }} spacing="md">
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

export default HealthSummary;
