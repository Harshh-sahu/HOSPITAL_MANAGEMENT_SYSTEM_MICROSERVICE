import { Avatar, Badge, Button, Divider, Modal, Table, TextInput, NumberInput } from "@mantine/core";
import { useDisclosure, useMediaQuery } from "@mantine/hooks";
import { IconEdit } from "@tabler/icons-react";
import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { useForm } from "@mantine/form";
import { DropzoneButton } from "../../Utility/Dropzone/DropzoneButton";
import useProtectedImage from "../../../Utility/useProtectedImage";
import { getAdmin, updateAdmin } from "../../../Service/AdminProfileService";
import { errorNotification, successNotification } from "../../../Utility/NotificationUtil";

function AdminProfile() {
  const user = useSelector((state: any) => state.user);
  const matches = useMediaQuery("(max-width: 768px)");
  const [opened, { open, close }] = useDisclosure(false);
  const [editmode, setEdit] = useState(false);
  const [profile, setProfile] = useState<any>({});

  useEffect(() => {
    if (user && user.id) {
      getAdmin(user.id)
        .then((data: any) => setProfile(data))
        .catch(() => {
          // For existing accounts with no profile yet, seed from JWT
          setProfile({ name: user.name, email: user.email, userId: user.id });
        });
    }
  }, [user]);

  const form = useForm({
    initialValues: {
      phone: "",
      address: "",
      profilePictureId: null as number | null,
    },
  });

  const handleEdit = () => {
    form.setValues({
      phone: profile.phone ?? "",
      address: profile.address ?? "",
      profilePictureId: profile.profilePictureId ?? null,
    });
    setEdit(true);
  };

  const handleSubmit = () => {
    const values = form.getValues();
    updateAdmin({
      ...profile,
      ...values,
      name: profile.name || user.name,
      email: profile.email || user.email,
      userId: profile.userId || user.id,
    })
      .then((data: any) => {
        setProfile(data);   // data now has the real id from DB
        setEdit(false);
        successNotification("Profile updated successfully");
      })
      .catch((error: any) => {
        errorNotification(error.response?.data?.message || "Failed to update profile");
      });
  };

  const previewId = editmode
    ? (form.values.profilePictureId ?? profile.profilePictureId)
    : profile.profilePictureId;
  const url = useProtectedImage(previewId);

  return (
    <div className="md:p-10 p-5">
      <div className="flex lg:flex-row flex-col justify-between items-center">
        <div className="flex gap-5 items-center">
          <div className="flex flex-col items-center gap-3">
            <Avatar
              variant="filled"
              size={matches ? 120 : 150}
              src={url}
              alt="admin avatar"
            />
            {editmode && (
              <Button size="sm" onClick={open} variant="filled">
                Upload
              </Button>
            )}
          </div>
          <div className="flex flex-col gap-3">
            <div className="md:text-2xl text-lg font-medium text-neutral-900">
              {user.name}
            </div>
            <div className="text-lg mb-2 text-neutral-700">{user.email}</div>
            <Badge color="blue" size="lg" variant="light">
              {user.role}
            </Badge>
          </div>
        </div>

        {!editmode ? (
          <Button
            type="button"
            size={matches ? "sm" : "lg"}
            onClick={handleEdit}
            variant="filled"
            leftSection={<IconEdit />}
          >
            Edit
          </Button>
        ) : (
          <Button
            size={matches ? "sm" : "lg"}
            variant="filled"
            color="green"
            onClick={handleSubmit}
          >
            Submit
          </Button>
        )}
      </div>

      <Divider my="xl" />

      <div>
        <div className="text-2xl font-medium text-neutral-900 mb-4">
          Account Information
        </div>
        <Table
          striped
          stripedColor="primary.2"
          verticalSpacing="md"
          withRowBorders={false}
        >
          <Table.Tbody className="[&>tr]:!mb-2 [&_td]:!w-1/2">
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">Name</Table.Th>
              <Table.Td className="text-xl">{user.name ?? "-"}</Table.Td>
            </Table.Tr>
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">Email</Table.Th>
              <Table.Td className="text-xl">{user.email ?? "-"}</Table.Td>
            </Table.Tr>
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">Role</Table.Th>
              <Table.Td className="text-xl">
                <Badge color="blue" variant="light">{user.role ?? "-"}</Badge>
              </Table.Td>
            </Table.Tr>
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">Phone</Table.Th>
              {editmode ? (
                <Table.Td>
                  <NumberInput
                    {...form.getInputProps("phone")}
                    label="Phone"
                    placeholder="Enter phone number"
                    hideControls
                    maxLength={10}
                  />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">{profile.phone ?? "-"}</Table.Td>
              )}
            </Table.Tr>
            <Table.Tr>
              <Table.Th className="font-semibold text-xl">Address</Table.Th>
              {editmode ? (
                <Table.Td>
                  <TextInput
                    {...form.getInputProps("address")}
                    label="Address"
                    placeholder="Enter address"
                  />
                </Table.Td>
              ) : (
                <Table.Td className="text-xl">{profile.address ?? "-"}</Table.Td>
              )}
            </Table.Tr>
          </Table.Tbody>
        </Table>
      </div>

      <Modal
        centered
        opened={opened}
        onClose={close}
        title={<span className="text-xl font-medium">Upload Profile Picture</span>}
      >
        <DropzoneButton close={close} form={form} id="profilePictureId" />
      </Modal>
    </div>
  );
}

export default AdminProfile;

