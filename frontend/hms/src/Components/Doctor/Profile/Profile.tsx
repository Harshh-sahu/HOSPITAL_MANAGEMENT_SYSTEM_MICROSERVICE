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
import {
  bloodGroups,
  doctorDepartments,
  doctorSpecializations,
} from "../../../Data/DropDownData";
import { useDisclosure } from "@mantine/hooks";
import { getDoctor, updateDoctor } from "../../../Service/DoctorProfileService";
import { error } from "console";
import { errorNotification, successNotification } from "../../../Utility/NotificationUtil";
import { parseToArray } from "../../../Utility/OtherUtility";
import { useForm } from "@mantine/form";
import { formatDate } from "../../../Utility/DateUtility";

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
  const [profile, setProfile] = useState<any>({});


   useEffect(()=>{
    getDoctor(user.profileId).then((data)=>{
      setProfile({...data});

    }).catch((error) => {
      console.log(error);
    });
   },[])

  const form = useForm({
    initialValues: {
      dob: '',
      phone: '',
      address: '',
      licenseNo: '',
      bloodGroup: '',
      department: '',
      totalExp: '',
    },
    validate: {
      dob: (value:any) =>
        !value ? "Date of birth is required" : undefined,
      phone: (value:any) =>
        !value ? "Phone number is required" : undefined,
      address: (value:any) => !value ? "Address is required" : undefined,
      licenseNo: (value:any) =>
        !value ? "License number is required" : undefined,
    },
  });
const handleEdit = () => {
  form.setValues({
    ...profile
  });
  setEdit(true);
};

const handleSubmit = (e: any) => {
  form.validate();  
  if (!form.isValid()) return;  // <-- Use ! to return only if form is invalid

  let values = form.getValues();
  console.log("Form submitted:", values);

updateDoctor({
  ...profile,
  ...values
})

    .then((_data) => {
      successNotification("Profile updated successfully");
     setProfile({
  ...profile,
  ...values,
});

      setEdit(false);
    })
    .catch((error) => {
      console.log(error);
      errorNotification(error.response.data.errorMessage);
    });

}
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

            {/* License Row */}
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">License No</Table.Th>
              {editmode ? (
                <Table.Td>
                  <TextInput
                    {...form.getInputProps("licenseNo")}
                    label="License No"
                    description="Enter your License number"
                    placeholder="License number"
            
                  />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">{profile.licenseNo?? "-"}</Table.Td>
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
                    {...form.getInputProps("specialization")}
                    label="Specializations"
                    description="Select your specializations"
                    data={doctorSpecializations}
                  />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">
                  {profile.specializations?? "-"}
                </Table.Td>
              )}
            </Table.Tr>

            {/* dep Row */}
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">Department</Table.Th>
              {editmode ? (
                <Table.Td>
                  <Select
                    {...form.getInputProps("department")}
                    label="Department"
                    description="Select your department"
                    data={doctorDepartments}
                  />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">
                  {profile.department?? "-"}
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
                          {...form.getInputProps("totalExp")}
                         maxLength={2}
                         max={50}
                       
                  />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">{profile.totalExp ?? "-"} {profile.totalExp ?? '-'} {profile.totalExp?'years':''}</Table.Td>
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
