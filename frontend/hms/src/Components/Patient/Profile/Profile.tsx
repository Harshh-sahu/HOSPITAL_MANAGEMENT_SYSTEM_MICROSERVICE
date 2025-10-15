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
import React, { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { bloodGroup, bloodGroups } from "../../../Data/DropDownData";
import { useDisclosure } from "@mantine/hooks";

import { getPatient, updatePatient } from "../../../Service/PatientProfileService";
import { formatDate } from "../../../Utility/DateUtility";
import { useForm } from "@mantine/form";
import { errorNotification, successNotification } from "../../../Utility/NotificationUtil";
import { arrayToCSV, parseToArray } from "../../../Utility/OtherUtility";

import { DropzoneButton } from "../../Utility/Dropzone/DropzoneButton";
import useProtectedImage from "../../../Utility/useProtectedImage";


function Profile() {
  const user = useSelector((state: any) => state.user);
  const [opened, { open, close }] = useDisclosure(false);
  const [editmode, setEdit] = useState(false);
  const [profile, setProfile] = useState<any>({});

useEffect(() => {
  if (user && user.profileId) {
    getPatient(user.profileId)
      .then((data) => {
        setProfile({
          ...data,
          allergies: parseToArray(data.allergies),
          chronicDisease: parseToArray(data.chronicDisease),
        });
        console.log("profile data", data);
      })
      .catch((error) => {
        console.log(error);
      });
  }
}, [user]);

  const form = useForm({
    initialValues: {
      dob: profile.dob,
      phone: profile.phone,
      address: profile.address,
      aadharNo: profile.aadharNo,
      profilePictureId: profile.profilePictureId,
      bloodGroup: profile.bloodGroup,
      allergies: profile.allergies,
      chronicDisease: profile.chronicDisease,
    },
    validate: {
      dob: (value: Date | null) =>
        !value ? "Date of birth is required" : undefined,
      phone: (value: number | undefined) =>
        !value ? "Phone number is required" : undefined,
      address: (value: string) => (!value ? "Address is required" : undefined),
      aadharNo: (value: number | undefined) =>
        !value ? "Aadhar number is required" : undefined,
    },
  });
const handleEdit = () => {
  form.setValues({
    ...profile,
    dob: profile.dob ? new Date(profile.dob) : undefined,
    chronicDisease: parseToArray(profile.chronicDisease),
    allergies: parseToArray(profile.allergies),
  });
  setEdit(true);
};
console.log(profile.profilePictureId);
const url =  useProtectedImage(profile.profilePictureId);

const handleSubmit = (e: any) => {
  form.validate();  
  if (!form.isValid()) return;  // <-- Use ! to return only if form is invalid

  let values = form.getValues();
  console.log("Form submitted:", values);

updatePatient({
  ...profile,
  ...values,
  dob: values.dob ? new Date(values.dob).toISOString().split('T')[0] : null,
  allergies: values.allergies ? JSON.stringify(values.allergies) : null,
  chronicDisease: values.chronicDisease ? JSON.stringify(values.chronicDisease) : null,
})

    .then((data) => {
      successNotification("Profile updated successfully");
     setProfile({
  ...data,
  allergies: parseToArray(data.allergies),
  chronicDisease: parseToArray(data.chronicDisease),
});

      setEdit(false);
    })
    .catch((error) => {
      console.log(error);
      errorNotification(error.response?.data?.message || "Failed to update profile");
    });
};


  return (
    <div className="p-10" >
      <div className="flex justify-between items-start">
        <div className="flex gap-5 items-center">
          <div className="flex flex-col items-center gap-3">
            <Avatar
              variant="filled"
              size={150}
              src={url}
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
            type="button"
            size="lg"
            onClick={handleEdit}
            variant="filled"
            leftSection={<IconEdit />}
          >
            Edit
          </Button>
        ) : (
          <Button onClick={handleSubmit} size="lg" type="submit" variant="filled">
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
          <Table.Tbody className="[&>tr]:!mb-2 [&_td]:!w-1/2">
            {/* DOB Row */}
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">
                Date of Birth
              </Table.Th>
              {editmode ? (
                <Table.Td>
                  <DateInput
                    {...form.getInputProps("dob")}
                    label="Date input"
                    placeholder="Date input"
                  />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">
                  {formatDate(profile.dob) ?? "-"}
                </Table.Td>
              )}
            </Table.Tr>

            {/* Phone Row */}
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">Phone</Table.Th>
              {editmode ? (
                <Table.Td>
                  <NumberInput
                    {...form.getInputProps("phone")}
                    label="Phone"
                    description="Enter your phone number"
                    hideControls
                    maxLength={10}
                    clampBehavior="strict"
                    placeholder="Phone number"
                  />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">{profile.phone ?? "-"}</Table.Td>
              )}
            </Table.Tr>

            {/* Address Row */}
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">Address</Table.Th>
              {editmode ? (
                <Table.Td>
                  <TextInput
                    {...form.getInputProps("address")}
                    label="Address"
                    description="Enter your address"
                    placeholder="Address"
                  />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">
                  {profile.address ?? "-"}
                </Table.Td>
              )}
            </Table.Tr>

            {/* Aadhar Row */}
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">Aadhar No</Table.Th>
              {editmode ? (
                <Table.Td>
                  <NumberInput
                    {...form.getInputProps("aadharNo")}
                    label="Aadhar No"
                    description="Enter your Aadhar number"
                    placeholder="Aadhar number"
                    maxLength={12}
                    clampBehavior="strict"
                    hideControls
                  />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">
                  {profile.aadharNo ?? "-"}
                </Table.Td>
              )}
            </Table.Tr>

            {/* Blood Group Row */}
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">Blood Group</Table.Th>
              {editmode ? (
                <Table.Td>
                  <Select
                    label="Blood Group"
                    description="Select your blood group"
                    data={bloodGroups}
                    {...form.getInputProps("bloodGroup")}
                  />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">
                  {bloodGroup[profile.bloodGroup] ?? "-"}
                </Table.Td>
              )}
            </Table.Tr>

            {/* Allergies Row */}
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">Allergies</Table.Th>
              {editmode ? (
                <Table.Td>
                  <TagsInput
                    {...form.getInputProps("allergies")}
                    label="please enter"
                    placeholder="Enter tag"
                    clearable
                  />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">
                  {arrayToCSV(profile.allergies) ?? "-"}
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
                    {...form.getInputProps("chronicDisease")}
                    clearable
                  />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">
                  {arrayToCSV(profile.chronicDisease) ?? "-"}
                </Table.Td>
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

        <DropzoneButton close={close} form={form} id="profilePictureId" />
      </Modal>
    </div>
  );
}

export default Profile;
