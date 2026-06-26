import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import {
  Accordion,
  Badge,
  Button,
  Card,
  Center,
  Group,
  Loader,
  Stack,
  Table,
  Text,
} from "@mantine/core";
import { IconPill, IconCalendar, IconUserHeart, IconDownload, IconFileTypePdf } from "@tabler/icons-react";
import {
  getPrescriptionByPatientId,
  getMedicinesByPrescriptionId,
} from "../../../Service/AppointmentService";
import { formatDateWithTime } from "../../../Utility/DateUtility";
import { exportToCSV, generatePrescriptionsPDF } from "../../../Utility/ExportUtil";

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
      .finally(() => {
        setLoading(false);
        setFetched(true);
      });
  };

  return (
    <Accordion.Item value={String(prescription.id)}>
      <Accordion.Control onClick={handleOpen}>
        <Group justify="space-between" wrap="nowrap">
          <Group gap={8}>
            <IconPill size={16} className="text-primary-500" />
            <Text size="sm" fw={600}>
              Prescription #{prescription.id}
            </Text>
          </Group>
          <Group gap={12} mr={8}>
            {prescription.doctorName && (
              <Group gap={4}>
                <IconUserHeart size={14} className="text-primary-400" />
                <Text size="xs" c="dimmed">{prescription.doctorName}</Text>
              </Group>
            )}
            {prescription.appointmentTime && (
              <Group gap={4}>
                <IconCalendar size={14} className="text-primary-400" />
                <Text size="xs" c="dimmed">
                  {formatDateWithTime(prescription.appointmentTime)}
                </Text>
              </Group>
            )}
          </Group>
        </Group>
      </Accordion.Control>
      <Accordion.Panel>
        {loading ? (
          <Center py={20}>
            <Loader size="sm" color="primary" />
          </Center>
        ) : medicines.length === 0 ? (
          <Text size="sm" c="dimmed" ta="center" py={10}>
            No medicines found for this prescription.
          </Text>
        ) : (
          <Table striped highlightOnHover withTableBorder withColumnBorders>
            <Table.Thead>
              <Table.Tr>
                <Table.Th>Medicine</Table.Th>
                <Table.Th>Dosage</Table.Th>
                <Table.Th>Duration</Table.Th>
                <Table.Th>Instructions</Table.Th>
              </Table.Tr>
            </Table.Thead>
            <Table.Tbody>
              {medicines.map((med: any, idx: number) => (
                <Table.Tr key={idx}>
                  <Table.Td>
                    <Badge color="primary" variant="light">
                      {med.medicineName || med.name || "—"}
                    </Badge>
                  </Table.Td>
                  <Table.Td>{med.dosage || "—"}</Table.Td>
                  <Table.Td>{med.duration || "—"}</Table.Td>
                  <Table.Td>{med.instructions || "—"}</Table.Td>
                </Table.Tr>
              ))}
            </Table.Tbody>
          </Table>
        )}
      </Accordion.Panel>
    </Accordion.Item>
  );
};

const Prescriptions = () => {
  const user = useSelector((state: any) => state.user);
  const [prescriptions, setPrescriptions] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getPrescriptionByPatientId(user.profileId)
      .then((data) => setPrescriptions(data))
      .catch((err) => console.error("Error fetching prescriptions:", err))
      .finally(() => setLoading(false));
  }, [user.profileId]);

  const [pdfLoading, setPdfLoading] = useState(false);

  const handleExportCSV = () => {
    const headers = ["#", "Prescription ID", "Doctor", "Date", "Notes"];
    const rows = prescriptions.map((p: any, i: number) => [
      i + 1,
      `#${p.id}`,
      p.doctorName ?? "",
      p.prescriptionDate
        ? new Date(p.prescriptionDate).toLocaleDateString("en-IN")
        : p.appointmentTime
        ? new Date(p.appointmentTime).toLocaleDateString("en-IN")
        : "",
      p.notes ?? "",
    ]);
    exportToCSV("Prescriptions", headers, rows);
  };

  const handleExportPDF = async () => {
    setPdfLoading(true);
    try {
      const enriched = await Promise.all(
        prescriptions.map(async (p: any) => {
          const medicines = await getMedicinesByPrescriptionId(p.id).catch(() => []);
          return { ...p, medicines };
        })
      );
      generatePrescriptionsPDF(enriched, user.name ?? "Patient");
    } finally {
      setPdfLoading(false);
    }
  };

  if (loading) {
    return (
      <Center h={300}>
        <Loader color="primary" />
      </Center>
    );
  }

  if (prescriptions.length === 0) {
    return (
      <Center h={300}>
        <Stack align="center" gap={8}>
          <IconPill size={48} stroke={1.2} className="text-gray-300" />
          <Text c="dimmed">No prescriptions found.</Text>
        </Stack>
      </Center>
    );
  }

  return (
    <Card shadow="sm" radius="lg" withBorder padding="lg">
      <Group justify="space-between" align="center" mb="md">
        <Text fw={600} size="lg" className="text-primary-600">
          My Prescriptions
        </Text>
        <Group gap={8}>
          <Button size="sm" variant="light" color="violet" leftSection={<IconDownload size={16} />} onClick={handleExportCSV}>
            Export CSV
          </Button>
          <Button
            size="sm"
            variant="gradient"
            gradient={{ from: "violet", to: "indigo" }}
            leftSection={<IconFileTypePdf size={16} />}
            onClick={handleExportPDF}
            loading={pdfLoading}
          >
            Export PDF
          </Button>
        </Group>
      </Group>
      <Accordion chevronPosition="right" variant="separated">
        {prescriptions.map((prescription: any) => (
          <PrescriptionItem key={prescription.id} prescription={prescription} />
        ))}
      </Accordion>
    </Card>
  );
};

export default Prescriptions;
