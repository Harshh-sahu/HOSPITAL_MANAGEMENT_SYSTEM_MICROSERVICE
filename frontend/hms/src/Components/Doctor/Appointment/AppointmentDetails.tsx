import {
  Badge,
  Breadcrumbs,
  Card,
  Divider,
  Group,
  MenuDivider,
  Tabs,
  Text,
  Title,
} from "@mantine/core";
import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getAppointmentDetails } from "../../../Service/AppointmentService";
import { formatDateWithTime } from "../../../Utility/DateUtility";
import {
    IconClipboardHeart,
  IconMessageCircle,
  IconPhoto,
  IconSettings,
  IconStethoscope,
  IconVaccine,
} from "@tabler/icons-react";
import ApReport from "./ApReport";
import Prescription from "./Prescription";

const AppointmentDetails = () => {
  const { id } = useParams();

  const [appointment, setAppointment] = useState<any>({});
  useEffect(() => {
    getAppointmentDetails(id)
      .then((data) => {
        setAppointment(data);
        console.log("Fetched appointment details:", data);
      })
      .catch((error) => {
        console.error("Error fetching appointment details:", error);
      });
  }, [id]);
  return (
    <div>
      <Breadcrumbs mb="md">
        <Link
          className="text-primary-400 hover:underline"
          to="/doctor/dashboard"
        >
          Dashboard
        </Link>
        <Link
          className="text-primary-400 hover:underline"
          to="/doctor/appointment"
        >
          Appointment{" "}
        </Link>
        <Text className="text-primary-400">Details</Text>
      </Breadcrumbs>

      <Card shadow="sm" radius="md" withBorder padding="lg">
        {/* Patient Name & Status */}
        <Group justify="space-between" mb="sm">
          <Title order={3}>{appointment.patientName}</Title>
          <Badge
            color={appointment.status === "SCHEDULED" ? "green" : "red"}
            variant="light"
          >
            {appointment.status}
          </Badge>
        </Group>

        {/* Patient Info */}
        <Text fw={700} size="sm" mt="sm">
          🧑 Patient Info
        </Text>
        <div className="grid grid-cols-2 gap-5">
          <Text>Email: {appointment.patientEmail}</Text>
          <Text>Phone: {appointment.patientPhone}</Text>
        </div>

        {/* Appointment Info */}
        <Text fw={700} size="sm" mt="md">
          📅 Appointment Info
        </Text>
        <div className="grid grid-cols-2 gap-5 mt-1">
          <Text>Reason: {appointment.reason}</Text>
          <Text>Time: {formatDateWithTime(appointment.appointmentTime)}</Text>
        </div>

        {/* Doctor Info */}
        <Text fw={700} size="sm" mt="md">
          👨‍⚕️ Doctor Info
        </Text>
        <Text>Doctor: {appointment.doctorName}</Text>

        {/* Notes */}
        <Text fw={700} size="sm" mt="md">
          📝 Notes
        </Text>
        <Text>{appointment.notes}</Text>
      </Card>

      <Tabs variant="pills" my="md" defaultValue="medical">
        <Tabs.List>
          <Tabs.Tab value="medical" leftSection={<IconStethoscope size={20} />}>
            Medical History
          </Tabs.Tab>
          <Tabs.Tab
            value="prescriptions"
            leftSection={<IconVaccine size={20} />}
          >
          Prescriptions
          </Tabs.Tab>
          <Tabs.Tab value="report" leftSection={<IconClipboardHeart size={20} />}>
             Reports
          </Tabs.Tab>
        </Tabs.List>
      <Divider my="md"/>
        <Tabs.Panel value="medical">Medical History</Tabs.Panel>

        <Tabs.Panel value="prescriptions">
          <Prescription appointment={appointment} />
        </Tabs.Panel>

        <Tabs.Panel value="report"> <ApReport appointment={appointment} /></Tabs.Panel>
      </Tabs>
    </div>
  );
};

export default AppointmentDetails;
