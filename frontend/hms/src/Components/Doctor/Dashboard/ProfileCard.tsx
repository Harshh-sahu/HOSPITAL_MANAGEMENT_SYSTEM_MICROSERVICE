import {
  Avatar,
  Badge,
  Button,
  Divider,
  Modal,
  NumberInput,
  Select,
  TextInput,
} from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";
import { IconEdit, IconCheck } from "@tabler/icons-react";
import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { useForm } from "@mantine/form";
import { getDoctor, updateDoctor } from "../../../Service/DoctorProfileService";
import useProtectedImage from "../../../Utility/useProtectedImage";
import { DropzoneButton } from "../../Utility/Dropzone/DropzoneButton";
import {
  errorNotification,
  successNotification,
} from "../../../Utility/NotificationUtil";
import { doctorDepartments, doctorSpecializations } from "../../../Data/DropDownData";
import { formatDate } from "../../../Utility/DateUtility";
import { DateInput } from "@mantine/dates";

const ProfileCard = () => {
  const user = useSelector((state: any) => state.user);
  const [profile, setProfile] = useState<any>({});
  const [editmode, setEdit] = useState(false);
  const [dpOpened, { open: openDp, close: closeDp }] = useDisclosure(false);

  useEffect(() => {
    if (user?.profileId) {
      getDoctor(user.profileId)
        .then((data: any) => setProfile(data))
        .catch((err: any) => console.error("Error fetching doctor profile:", err));
    }
  }, [user?.profileId]);

  const form = useForm({
    initialValues: {
      dob: "" as any,
      phone: "" as any,
      address: "",
      licenseNo: "",
      specialization: "",
      department: "",
      totalExp: "" as any,
      profilePictureId: null as number | null,
    },
    validate: {
      phone: (v: any) => (!v ? "Phone is required" : undefined),
      address: (v: any) => (!v ? "Address is required" : undefined),
      licenseNo: (v: any) => (!v ? "License No is required" : undefined),
    },
  });

  const handleEdit = () => {
    form.setValues({
      dob: profile.dob ? new Date(profile.dob) : undefined,
      phone: profile.phone ?? "",
      address: profile.address ?? "",
      licenseNo: profile.licenseNo ?? "",
      specialization: profile.specialization ?? "",
      department: profile.department ?? "",
      totalExp: profile.totalExp ?? "",
      profilePictureId: profile.profilePictureId ?? null,
    });
    setEdit(true);
  };

  const handleSubmit = () => {
    form.validate();
    if (!form.isValid()) return;
    const values = form.getValues();
    updateDoctor({ ...profile, ...values })
      .then((data: any) => {
        setProfile(data);
        setEdit(false);
        successNotification("Profile updated successfully");
      })
      .catch((err: any) =>
        errorNotification(err.response?.data?.errorMessage || "Update failed")
      );
  };

  const previewId = editmode
    ? (form.values.profilePictureId ?? profile.profilePictureId)
    : profile.profilePictureId;
  const url = useProtectedImage(previewId);

  return (
    <div className="p-5 border shadow-sm rounded-xl bg-white flex flex-col gap-4">
      {/* Header */}
      <div className="flex justify-between items-start">
        <div className="flex gap-4 items-center">
          <div className="flex flex-col items-center gap-2">
            <Avatar src={url} size={80} radius="xl" alt="profile" />
            {editmode && (
              <Button size="xs" variant="light" onClick={openDp}>
                Change Photo
              </Button>
            )}
          </div>
          <div>
            <div className="font-semibold text-lg text-gray-900">{user.name}</div>
            <div className="text-sm text-gray-500">{user.email}</div>
            {profile.specialization && (
              <Badge color="blue" variant="light" size="sm" mt={4}>
                {profile.specialization}
              </Badge>
            )}
          </div>
        </div>
        {!editmode ? (
          <Button
            size="xs"
            variant="light"
            leftSection={<IconEdit size={14} />}
            onClick={handleEdit}
          >
            Edit
          </Button>
        ) : (
          <Button
            size="xs"
            color="green"
            leftSection={<IconCheck size={14} />}
            onClick={handleSubmit}
          >
            Save
          </Button>
        )}
      </div>

      <Divider />

      {/* Info grid */}
      <div className="grid grid-cols-2 gap-3 text-sm">
        {/* Phone */}
        <div>
          <div className="text-gray-400 text-xs mb-1">Phone</div>
          {editmode ? (
            <NumberInput
              {...form.getInputProps("phone")}
              placeholder="Phone"
              hideControls
              size="xs"
              maxLength={10}
            />
          ) : (
            <div className="font-medium">{profile.phone ?? "-"}</div>
          )}
        </div>

        {/* Department */}
        <div>
          <div className="text-gray-400 text-xs mb-1">Department</div>
          {editmode ? (
            <Select
              {...form.getInputProps("department")}
              data={doctorDepartments}
              placeholder="Department"
              size="xs"
            />
          ) : (
            <div className="font-medium">{profile.department ?? "-"}</div>
          )}
        </div>

        {/* Experience */}
        <div>
          <div className="text-gray-400 text-xs mb-1">Experience</div>
          {editmode ? (
            <NumberInput
              {...form.getInputProps("totalExp")}
              placeholder="Years"
              hideControls
              size="xs"
              max={50}
            />
          ) : (
            <div className="font-medium">
              {profile.totalExp ? `${profile.totalExp} yrs` : "-"}
            </div>
          )}
        </div>

        {/* License */}
        <div>
          <div className="text-gray-400 text-xs mb-1">License No</div>
          {editmode ? (
            <TextInput
              {...form.getInputProps("licenseNo")}
              placeholder="License No"
              size="xs"
            />
          ) : (
            <div className="font-medium">{profile.licenseNo ?? "-"}</div>
          )}
        </div>

        {/* DOB */}
        <div>
          <div className="text-gray-400 text-xs mb-1">Date of Birth</div>
          {editmode ? (
            <DateInput
              {...form.getInputProps("dob")}
              placeholder="DOB"
              size="xs"
            />
          ) : (
            <div className="font-medium">{formatDate(profile.dob) ?? "-"}</div>
          )}
        </div>

        {/* Specialization */}
        <div>
          <div className="text-gray-400 text-xs mb-1">Specialization</div>
          {editmode ? (
            <Select
              {...form.getInputProps("specialization")}
              data={doctorSpecializations}
              placeholder="Specialization"
              size="xs"
            />
          ) : (
            <div className="font-medium">{profile.specialization ?? "-"}</div>
          )}
        </div>

        {/* Address — full width */}
        <div className="col-span-2">
          <div className="text-gray-400 text-xs mb-1">Address</div>
          {editmode ? (
            <TextInput
              {...form.getInputProps("address")}
              placeholder="Address"
              size="xs"
            />
          ) : (
            <div className="font-medium">{profile.address ?? "-"}</div>
          )}
        </div>
      </div>

      {/* DP Upload Modal */}
      <Modal
        centered
        opened={dpOpened}
        onClose={closeDp}
        title={<span className="text-lg font-medium">Upload Profile Picture</span>}
      >
        <DropzoneButton close={closeDp} form={form} id="profilePictureId" />
      </Modal>
    </div>
  );
};

export default ProfileCard;
