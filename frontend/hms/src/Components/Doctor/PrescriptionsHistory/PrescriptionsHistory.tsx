import { useEffect, useState } from "react";
import {
  Accordion,
  Badge,
  Card,
  Center,
  Group,
  Loader,
  Stack,
  Table,
  Text,
  TextInput,
} from "@mantine/core";
import { IconPill, IconCalendar, IconUserHeart, IconSearch } from "@tabler/icons-react";
import {
  getAllPrescriptions,
  getMedicinesByPrescriptionId,
} from "../../../Service/AppointmentService";

const PrescriptionItem = ({ prescription }: { prescription: any }) => {
  const [medicines, setMedicines] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [fetched, setFetched] = useState(false);

  const handleOpen = () => {
    if (fetched) return;
    setLoading(true);
    getMedicinesByPrescriptionId(prescription.id)
      .then((data) => setMedicines(data))
      .catch((err) => console.error("Error fetching medicines:", err))
      .finally(() => { setLoading(false); setFetched(true); });
  };

  return (
    <Accordion.Item value={String(prescription.id)}>
      <Accordion.Control onClick={handleOpen}>
        <Group justify="space-between" wrap="nowrap">
          <Group gap={8}>
            <IconPill size={16} className="text-primary-500" />
            <Text size="sm" fw={600}>Prescription #{prescription.id}</Text>
          </Group>
          <Group gap={12} mr={8}>
            {prescription.patientName && (
              <Group gap={4}>
                <IconUserHeart size={14} className="text-gray-400" />
                <Text size="xs" c="dimmed">{prescription.patientName}</Text>
              </Group>
            )}
            {prescription.prescriptionDate && (
              <Group gap={4}>
                <IconCalendar size={14} className="text-gray-400" />
                <Text size="xs" c="dimmed">
                  {new Date(prescription.prescriptionDate).toLocaleDateString()}
                </Text>
              </Group>
            )}
          </Group>
        </Group>
      </Accordion.Control>
      <Accordion.Panel>
        {loading ? (
          <Center py="sm"><Loader size="xs" color="primary" /></Center>
        ) : medicines.length === 0 ? (
          <Text size="sm" c="dimmed" py="xs">No medicines recorded.</Text>
        ) : (
          <Table striped withTableBorder fz="sm">
            <Table.Thead>
              <Table.Tr>
                <Table.Th>Medicine</Table.Th>
                <Table.Th>Dosage</Table.Th>
                <Table.Th>Frequency</Table.Th>
                <Table.Th>Duration</Table.Th>
                <Table.Th>Type</Table.Th>
              </Table.Tr>
            </Table.Thead>
            <Table.Tbody>
              {medicines.map((med: any) => (
                <Table.Tr key={med.id}>
                  <Table.Td fw={500}>{med.medicineName}</Table.Td>
                  <Table.Td>{med.dosage ?? "—"}</Table.Td>
                  <Table.Td>{med.frequency ?? "—"}</Table.Td>
                  <Table.Td>
                    {med.duration ? (
                      <Badge variant="light" color="primary" size="xs">{med.duration} days</Badge>
                    ) : "—"}
                  </Table.Td>
                  <Table.Td>{med.medicineType ?? "—"}</Table.Td>
                </Table.Tr>
              ))}
            </Table.Tbody>
          </Table>
        )}
        {prescription.notes && (
          <Card withBorder radius="sm" p="xs" mt="xs" bg="yellow.0">
            <Text size="xs" c="dimmed" fw={500}>Notes:</Text>
            <Text size="xs">{prescription.notes}</Text>
          </Card>
        )}
      </Accordion.Panel>
    </Accordion.Item>
  );
};

const PrescriptionsHistory = () => {
  const [prescriptions, setPrescriptions] = useState<any[]>([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getAllPrescriptions()
      .then((data) => setPrescriptions(data))
      .catch((err) => console.error("Error fetching prescriptions:", err))
      .finally(() => setLoading(false));
  }, []);

  const filtered = prescriptions.filter((p: any) =>
    (p.patientName ?? "").toLowerCase().includes(search.toLowerCase()) ||
    String(p.id).includes(search)
  );

  if (loading) return <Center h={300}><Loader color="primary" /></Center>;

  return (
    <Stack gap="md">
      <TextInput
        placeholder="Search by patient name or ID..."
        leftSection={<IconSearch size={16} />}
        value={search}
        onChange={(e) => setSearch(e.currentTarget.value)}
        radius="md"
      />
      {filtered.length === 0 ? (
        <Center h={200}>
          <Stack align="center" gap={8}>
            <IconPill size={48} stroke={1.2} className="text-gray-300" />
            <Text c="dimmed">No prescriptions found.</Text>
          </Stack>
        </Center>
      ) : (
        <Accordion variant="separated" radius="md">
          {filtered.map((p: any) => <PrescriptionItem key={p.id} prescription={p} />)}
        </Accordion>
      )}
    </Stack>
  );
};

export default PrescriptionsHistory;
