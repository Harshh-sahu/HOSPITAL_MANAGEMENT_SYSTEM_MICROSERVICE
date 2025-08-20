import { Avatar, Button, Divider, Modal, NumberInput, Select, Table, TagsInput, TextInput } from "@mantine/core";
import { DateInput } from "@mantine/dates";
import { IconEdit } from "@tabler/icons-react";
import React, { useState } from "react";
import { useSelector } from "react-redux";
import { bloodGroups } from "../../../Data/DropDownData";
import { useDisclosure } from "@mantine/hooks";

const patient: any = {
  dob: "1998-05-14",
  phone: "+91 9876543210",
  address: "123, MG Road, Bhopal",
  aadharNo: "1234-5678-9012",
  bloodGroup: "O+",
  allergies: "Pollen, Dust",
  chronicDisease: "Diabetes",
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

          <Avatar variant="filled" size={150} src="/avatar.png" alt="it's me" />
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
                <Table.Td className="text-xl">{patient.dob}</Table.Td>
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
                <Table.Td className="text-xl">{patient.phone}</Table.Td>
              )}
            </Table.Tr>

            {/* Address Row */}
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">Address</Table.Th>
              {editmode ? (
                <Table.Td>
                  <TextInput label="Address" 
                  description="Enter your address"
                  placeholder="Address" />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">{patient.address}</Table.Td>
              )}
            </Table.Tr>

            {/* Aadhar Row */}
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">Aadhar No</Table.Th>
              {editmode ? (
                <Table.Td>
                   <NumberInput
      label="Aadhar No"
      description="Enter your Aadhar number"
      placeholder="Aadhar number"
       maxLength={12}
                    clampBehavior="strict"
      hideControls
    />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">{patient.aadharNo}</Table.Td>
              )}
            </Table.Tr>

            {/* Blood Group Row */}
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">Blood Group</Table.Th>
              {editmode ? (
                <Table.Td>
                  <Select label="Blood Group"
                  description="Select your blood group"
                  data={bloodGroups}  />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">{patient.bloodGroup}</Table.Td>
              )}
            </Table.Tr>

            {/* Allergies Row */}
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">Allergies</Table.Th>
              {editmode ? (
                <Table.Td>
                 <TagsInput
      label="please enter"
      placeholder="Enter tag"
      
      clearable
    />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">
                  {patient.allergies || "None"}
                </Table.Td>
              )}
            </Table.Tr>

            {/* Chronic Disease Row */}
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">
                Chronic Disease
              </Table.Th>
              {editmode ? (
                <Table.Td>
                 <TagsInput
      label="please enter"
      placeholder="Enter tag"
     
      clearable
    />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">
                  {patient.chronicDisease}
                </Table.Td>
              )}
            </Table.Tr>
          </Table.Tbody>
        </Table>
      </div>
            <Modal centered opened={opened} onClose={close} title={<span className="text-xl font-medium">Upload Profile Picture</span>}>
        {/* Modal content */}
        
      </Modal>
    </div>
  );
}

export default Profile;
