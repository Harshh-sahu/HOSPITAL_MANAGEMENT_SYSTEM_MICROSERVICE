import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { Badge, Card, Text, Loader, Center, Stack, Group } from "@mantine/core";
import {
  IconFileReport,
  IconUserHeart,
  IconCalendar,
  IconStethoscope,
  IconActivity,
  IconExternalLink,
} from "@tabler/icons-react";
import { getReportByPatientId } from "../../../Service/AppointmentService";
import { formatDateWithTime } from "../../../Utility/DateUtility";

const MedicalRecords = () => {
  const user = useSelector((state: any) => state.user);
  const [records, setRecords] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getReportByPatientId(user.profileId)
      .then((data) => setRecords(data))
      .catch((err) => console.error("Error fetching medical records:", err))
      .finally(() => setLoading(false));
  }, [user.profileId]);

  if (loading) {
    return (
      <Center h={300}>
        <Loader color="primary" />
      </Center>
    );
  }

  if (records.length === 0) {
    return (
      <Center h={300}>
        <Stack align="center" gap={8}>
          <IconFileReport size={48} stroke={1.2} className="text-gray-300" />
          <Text c="dimmed">No medical records found.</Text>
        </Stack>
      </Center>
    );
  }

  return (
    <div className="grid lg:grid-cols-2 grid-cols-1 gap-5">
      {records.map((record: any) => (
        <Card key={record.id} shadow="sm" radius="lg" withBorder padding="lg">
          <Stack gap={10}>
            <Group justify="space-between" align="center">
              <Text fw={600} size="md" className="text-primary-600">
                Visit Report
              </Text>
              <Badge color="primary" variant="light">
                #{record.id}
              </Badge>
            </Group>

            <Group gap={8}>
              <IconUserHeart size={16} className="text-primary-500" />
              <Text size="sm" c="dimmed">Doctor:</Text>
              <Text size="sm" fw={500}>{record.doctorName || "N/A"}</Text>
            </Group>

            <Group gap={8}>
              <IconStethoscope size={16} className="text-primary-500" />
              <Text size="sm" c="dimmed">Diagnosis:</Text>
              <Text size="sm" fw={500}>{record.diagnosis || "N/A"}</Text>
            </Group>

            {record.symptoms && record.symptoms.length > 0 && (
              <Group gap={8} wrap="wrap">
                <IconActivity size={16} className="text-primary-500" />
                <Text size="sm" c="dimmed">Symptoms:</Text>
                {record.symptoms.map((s: string, i: number) => (
                  <Badge key={i} color="orange" variant="light" size="sm">{s}</Badge>
                ))}
              </Group>
            )}

            <Group gap={8}>
              <IconFileReport size={16} className="text-primary-500" />
              <Text size="sm" c="dimmed">Notes:</Text>
              <Text size="sm">{record.notes || "—"}</Text>
            </Group>

            {record.referral && record.referral !== "None" && (
              <Group gap={8}>
                <IconExternalLink size={16} className="text-primary-500" />
                <Text size="sm" c="dimmed">Referral:</Text>
                <Badge color="grape" variant="light" size="sm">{record.referral}</Badge>
              </Group>
            )}

            <Group gap={8}>
              <IconCalendar size={16} className="text-primary-500" />
              <Text size="sm" c="dimmed">Created:</Text>
              <Text size="sm">{record.createdAt ? formatDateWithTime(record.createdAt) : "—"}</Text>
            </Group>

            {record.followUpDate && (
              <Group gap={8}>
                <IconCalendar size={16} className="text-orange-400" />
                <Text size="sm" c="dimmed">Follow-up:</Text>
                <Text size="sm" c="orange">{record.followUpDate}</Text>
              </Group>
            )}
          </Stack>
        </Card>
      ))}
    </div>
  );
};

export default MedicalRecords;
