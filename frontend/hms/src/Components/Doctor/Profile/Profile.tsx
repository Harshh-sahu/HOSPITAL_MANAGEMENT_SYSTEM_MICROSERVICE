import {
  Avatar,
  Button,
  Divider,
  Modal,
  NumberInput,
  Select,
  Table,
  TagsInput,
  TextInput,
} from "@mantine/core";
import { DateInput } from "@mantine/dates";
import { IconEdit } from "@tabler/icons-react";
import React, { useState } from "react";
import { useSelector } from "react-redux";
import {
  bloodGroups,
  doctorDepartments,
  doctorSpecializations,
} from "../../../Data/DropDownData";
import { useDisclosure } from "@mantine/hooks";

const doctor: any = {
  name: "Dr. Rajesh Kumar",
  email: "rajesh.kumar@example.com",
  dob: "1980-08-25",
  phone: "+91 9876543210",
  address: "45, Civil Lines, Bhopal",
  licenseNo: "MPMC123456",
  specialization: "Cardiology",
  department: "Cardiology",
  totalExp: 15
};


function Profile() {
  const user = useSelector((state: any) => state.user);
  const [opened, { open, close }] = useDisclosure(false);
  const [editmode, setEdit] = useState(false);

  return (
    <div className="p-10">
      <div className="flex justify-between items-start">
        <div className="flex gap-5 items-center">
          <div className="flex flex-col items-center gap-3">
            <Avatar
              variant="filled"
              size={150}
              src="/avatar.png"
              alt="it's me"
            />
            {editmode && (
              <Button size="sm" onClick={open} variant="filled">
                Upload
              </Button>
            )}
          </div>
          <div className="flex flex-col gap-3">
            <div className="text-3xl font-medium text-neutral-900">
              {user.name}
            </div>
            <div className="text-xl mb-5 text-neutral-700">{user.email}</div>
          </div>
        </div>
        {!editmode ? (
          <Button
            size="lg"
            onClick={() => setEdit(true)}
            variant="filled"
            leftSection={<IconEdit />}
          >
            Edit
          </Button>
        ) : (
          <Button size="lg" onClick={() => setEdit(false)} variant="filled">
            Submit
          </Button>
        )}
      </div>

      <Divider my="xl" />

      <div>
        <div className="text-2xl font-medium text-neutral-900">
          Personal Information
        </div>

        <Table
          striped
          stripedColor="primary.2"
          verticalSpacing="md"
          withRowBorders={false}
        >
          <Table.Tbody>
            {/* DOB Row */}
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">
                Date of Birth
              </Table.Th>
              {editmode ? (
                <Table.Td>
                  <DateInput label="Date input" placeholder="Date input" />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">{doctor.dob}</Table.Td>
              )}
            </Table.Tr>

            {/* Phone Row */}
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">Phone</Table.Th>
              {editmode ? (
                <Table.Td>
                  <NumberInput
                    label="Phone"
                    description="Enter your phone number"
                    hideControls
                    maxLength={10}
                    clampBehavior="strict"
                    placeholder="Phone number"
                  />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">{doctor.phone}</Table.Td>
              )}
            </Table.Tr>

            {/* Address Row */}
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">Address</Table.Th>
              {editmode ? (
                <Table.Td>
                  <TextInput
                    label="Address"
                    description="Enter your address"
                    placeholder="Address"
                  />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">{doctor.address}</Table.Td>
              )}
            </Table.Tr>

            {/* License Row */}
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">License No</Table.Th>
              {editmode ? (
                <Table.Td>
                  <NumberInput
                    label="License No"
                    description="Enter your License number"
                    placeholder="License number"
                    maxLength={12}
                    clampBehavior="strict"
                    hideControls
                  />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">{doctor.aadharNo}</Table.Td>
              )}
            </Table.Tr>

            {/* Specializations Row */}
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">
                Specializations
              </Table.Th>
              {editmode ? (
                <Table.Td>
                  <Select
                    label="Specializations"
                    description="Select your specializations"
                    data={doctorSpecializations}
                  />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">
                  {doctor.specializations}
                </Table.Td>
              )}
            </Table.Tr>

            {/* dep Row */}
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">Department</Table.Th>
              {editmode ? (
                <Table.Td>
                  <Select
                    label="Department"
                    description="Select your department"
                    data={doctorDepartments}
                  />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">
                  {doctor.department || "None"}
                </Table.Td>
              )}
            </Table.Tr>

            {/* experience Row */}
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">Experience</Table.Th>
              {editmode ? (
                <Table.Td>
                  <NumberInput
                    label="Experience (in years)"
                    placeholder="Enter experience"
                    clampBehavior="strict"
                         hideControls
                         maxLength={2}
                         max={50}
                  />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">{doctor.totalExp} years</Table.Td>
              )}
            </Table.Tr>
          </Table.Tbody>
        </Table>
      </div>
      <Modal
        centered
        opened={opened}
        onClose={close}
        title={
          <span className="text-xl font-medium">Upload Profile Picture</span>
        }
      >
        {/* Modal content */}
      </Modal>
    </div>
  );
}

export default Profile;
