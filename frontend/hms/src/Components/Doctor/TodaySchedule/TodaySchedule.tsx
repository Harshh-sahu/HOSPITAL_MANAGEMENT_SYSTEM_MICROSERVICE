import { useEffect, useState } from "react";
import {
  Badge,
  Card,
  Text,
  Loader,
  Center,
  Stack,
  Group,
  Avatar,
  Divider,
} from "@mantine/core";
import {
  IconCalendarCheck,
  IconClock,
  IconUser,
  IconStethoscope,
} from "@tabler/icons-react";
import { getTodaysAppointments } from "../../../Service/AppointmentService";

const statusColor: Record<string, string> = {
  SCHEDULED: "blue",
  COMPLETED: "green",
  CANCELLED: "red",
  PENDING: "yellow",
};

const TodaySchedule = () => {
  const [appointments, setAppointments] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getTodaysAppointments()
      .then((data) => setAppointments(data))
      .catch((err) => console.error("Error fetching today's appointments:", err))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <Center h={300}>
        <Loader color="primary" />
      </Center>
    );
  }

  if (appointments.length === 0) {
    return (
      <Center h={300}>
        <Stack align="center" gap={8}>
          <IconCalendarCheck size={48} stroke={1.2} className="text-gray-300" />
          <Text c="dimmed">No appointments scheduled for today.</Text>
        </Stack>
      </Center>
    );
  }

  return (
    <Stack gap="md">
      {appointments.map((appt: any, index: number) => (
        <Card key={appt.id ?? index} withBorder radius="md" p="md" shadow="xs">
          <Group justify="space-between" align="flex-start">
            <Group gap="sm">
              <Avatar color="primary" radius="xl" size="md">
                <IconUser size={18} />
              </Avatar>
              <div>
                <Text fw={600} size="sm">
                  {appt.patientName ?? `Patient #${appt.patientId}`}
                </Text>
                <Text size="xs" c="dimmed">
                  {appt.reason ?? "General Consultation"}
                </Text>
              </div>
            </Group>
            <Badge color={statusColor[appt.status] ?? "gray"} variant="light" size="sm">
              {appt.status}
            </Badge>
          </Group>

          <Divider my="xs" />

          <Group gap="xl">
            <Group gap={6}>
              <IconClock size={14} className="text-gray-400" />
              <Text size="xs" c="dimmed">
                {appt.appointmentTime
                  ? new Date(appt.appointmentTime).toLocaleTimeString([], {
                      hour: "2-digit",
                      minute: "2-digit",
                    })
                  : "—"}
              </Text>
            </Group>
            <Group gap={6}>
              <IconStethoscope size={14} className="text-gray-400" />
              <Text size="xs" c="dimmed">
                {appt.doctorName ?? "—"}
              </Text>
            </Group>
          </Group>
        </Card>
      ))}
    </Stack>
  );
};

export default TodaySchedule;
