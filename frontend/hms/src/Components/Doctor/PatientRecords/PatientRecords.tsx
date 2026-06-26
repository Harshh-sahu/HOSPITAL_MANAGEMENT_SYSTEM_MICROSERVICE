import { useEffect, useState } from "react";
import {
  Accordion,
  Badge,
  Card,
  Center,
  Group,
  Loader,
  Stack,
  Text,
  TextInput,
} from "@mantine/core";
import {
  IconUser,
  IconSearch,
  IconFileReport,
  IconCalendar,
  IconStethoscope,
  IconActivity,
} from "@tabler/icons-react";
import { getAllPatient } from "../../../Service/PatientProfileService";
import { getReportByPatientId } from "../../../Service/AppointmentService";

const PatientRecordItem = ({ patient }: { patient: any }) => {
  const [records, setRecords] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [fetched, setFetched] = useState(false);

  const handleOpen = () => {
    if (fetched) return;
    setLoading(true);
    getReportByPatientId(patient.id)
      .then((data) => setRecords(data))
      .catch((err) => console.error("Error fetching records:", err))
      .finally(() => { setLoading(false); setFetched(true); });
  };

  const name = patient.firstName
    ? `${patient.firstName} ${patient.lastName ?? ""}`.trim()
    : patient.name ?? `Patient #${patient.id}`;

  return (
    <Accordion.Item value={String(patient.id)}>
      <Accordion.Control onClick={handleOpen}>
        <Group gap={10}>
          <div className="p-1.5 rounded-full bg-primary-50">
            <IconUser size={16} className="text-primary-500" />
          </div>
          <div>
            <Text size="sm" fw={600}>{name}</Text>
            {patient.bloodGroup && (
              <Text size="xs" c="dimmed">Blood: {patient.bloodGroup}</Text>
            )}
          </div>
        </Group>
      </Accordion.Control>
      <Accordion.Panel>
        {loading ? (
          <Center py="sm"><Loader size="xs" color="primary" /></Center>
        ) : records.length === 0 ? (
          <Text size="sm" c="dimmed" py="xs">No medical records found for this patient.</Text>
        ) : (
          <Stack gap="sm">
            {records.map((rec: any) => (
              <Card key={rec.id} withBorder radius="sm" p="sm" bg="gray.0">
                <Group justify="space-between" mb={6}>
                  <Group gap={6}>
                    <IconFileReport size={14} className="text-primary-500" />
                    <Text size="sm" fw={600}>{rec.diagnosis ?? "Diagnosis not recorded"}</Text>
                  </Group>
                  {rec.createdAt && (
                    <Group gap={4}>
                      <IconCalendar size={13} className="text-gray-400" />
                      <Text size="xs" c="dimmed">
                        {new Date(rec.createdAt).toLocaleDateString()}
                      </Text>
                    </Group>
                  )}
                </Group>

                {rec.symptoms && rec.symptoms.length > 0 && (
                  <Group gap={4} mb={4}>
                    <IconActivity size={13} className="text-gray-400" />
                    {rec.symptoms.map((s: string) => (
                      <Badge key={s} variant="light" color="blue" size="xs">{s}</Badge>
                    ))}
                  </Group>
                )}

                {rec.notes && (
                  <Text size="xs" c="dimmed">{rec.notes}</Text>
                )}

                {rec.doctorName && (
                  <Group gap={4} mt={4}>
                    <IconStethoscope size={13} className="text-gray-400" />
                    <Text size="xs" c="dimmed">{rec.doctorName}</Text>
                  </Group>
                )}
              </Card>
            ))}
          </Stack>
        )}
      </Accordion.Panel>
    </Accordion.Item>
  );
};

const PatientRecords = () => {
  const [patients, setPatients] = useState<any[]>([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getAllPatient()
      .then((data) => setPatients(data))
      .catch((err) => console.error("Error fetching patients:", err))
      .finally(() => setLoading(false));
  }, []);

  const filtered = patients.filter((p: any) => {
    const name = p.firstName
      ? `${p.firstName} ${p.lastName ?? ""}`.trim()
      : p.name ?? "";
    return name.toLowerCase().includes(search.toLowerCase());
  });

  if (loading) return <Center h={300}><Loader color="primary" /></Center>;

  return (
    <Stack gap="md">
      <TextInput
        placeholder="Search patient by name..."
        leftSection={<IconSearch size={16} />}
        value={search}
        onChange={(e) => setSearch(e.currentTarget.value)}
        radius="md"
      />

      {filtered.length === 0 ? (
        <Center h={200}>
          <Stack align="center" gap={8}>
            <IconFileReport size={48} stroke={1.2} className="text-gray-300" />
            <Text c="dimmed">No patients found.</Text>
          </Stack>
        </Center>
      ) : (
        <Accordion variant="separated" radius="md">
          {filtered.map((p: any) => (
            <PatientRecordItem key={p.id} patient={p} />
          ))}
        </Accordion>
      )}
    </Stack>
  );
};

export default PatientRecords;
