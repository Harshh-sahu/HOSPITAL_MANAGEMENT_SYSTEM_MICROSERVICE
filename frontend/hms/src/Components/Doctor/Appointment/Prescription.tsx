import { ActionIcon, Button, Card, Divider, Fieldset, Grid, Modal, NumberInput, Select, SelectProps, SegmentedControl, Text, TextInput, Title } from "@mantine/core";
import { IconCheck, IconEye, IconLayoutGrid, IconMedicineSyrup, IconPlus, IconSearch, IconTable, IconTrash } from "@tabler/icons-react";
import { FilterMatchMode } from "primereact/api";
import { Column } from "primereact/column";
import { DataTable, DataTableFilterMeta } from "primereact/datatable";
import React, { useEffect, useState } from "react";
import { createAppointmentReport, getPrescriptionByPatientId, isReportExists } from "../../../Service/AppointmentService";
import { formatDate } from "../../../Utility/DateUtility";
import { useNavigate } from "react-router-dom";
import { useDisclosure, useMediaQuery } from "@mantine/hooks";
import { Toolbar } from "primereact/toolbar";
import PresCard from "./PresCard";
import { useForm } from "@mantine/form";
import { dosageFrequencies, medicineTypes } from "../../../Data/DropDownData";
import { getAllMedicines } from "../../../Service/MedicineService";
import { errorNotification, successNotification } from "../../../Utility/NotificationUtil";

const Prescription = ({ appointment }: any) => {
  const [view,setView] = useState<string>("table");
  const [opened,{open,close}] = useDisclosure(false);
  const [addOpened,{open:openAdd,close:closeAdd}] = useDisclosure(false);
  const matches = useMediaQuery('(max-width: 768px)');
  const [data, setData] = useState<any[]>([]);
  const [allowAdd, setAllowAdd] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(false);
  const [medicine, setMedicine] = useState<any[]>([]);
  const [medicineMap, setMedicineMap] = useState<Record<string, any>>({});
  const [filters, setFilters] = useState<DataTableFilterMeta>({
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
  });

  type Medicine = {
    name: string;
    medicineId?: string | number | undefined;
    dosage: string;
    frequency: string;
    route: string;
    type: string;
    instructions: string;
  };

  const form = useForm<any>({
    initialValues: {
      medicines: [] as Medicine[],
      notes: "",
    },
    validate: {
      medicines: {
        name: (value: any) => (value?.trim() ? null : "Medicine name is required"),
        dosage: (value: any) => (value?.trim() ? null : "Dosage is required"),
        frequency: (value: any) => (value?.trim() ? null : "Frequency is required"),
        type: (value: any) => (value?.trim() ? null : "Type is required"),
        instructions: (value: any) => (value?.trim() ? null : "Instructions are required"),
      },
    },
  });

  const actionBodyTemplate = (rowData:any) => {
    return <div className="flex gap-2">
        <ActionIcon onClick={()=> navigate("/doctor/appointment/"+rowData.appointmentId)}>
            <IconEye size={20} stroke={1.5} />
        </ActionIcon>
        <ActionIcon onClick={()=>handleMedicine(rowData.medicines)}>
            <IconMedicineSyrup size={20} stroke={1.5} />
        </ActionIcon>
    </div>
  };

  const [medicineData,setMedicineData] = useState<any[]>([]);

  const onGlobalFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    let _filters: any = { ...filters };
    _filters["global"].value = value;
    setFilters(_filters);
    setGlobalFilterValue(value);
  };

  const navigate = useNavigate();
  const [globalFilterValue, setGlobalFilterValue] = useState<string>("");

  const fetchData = () => {
    if (!appointment?.patientId) return;
    getPrescriptionByPatientId(appointment?.patientId)
      .then((res) => setData(res))
      .catch((err) => console.error("Error fetching Prescription data:", err));
    isReportExists(appointment?.id)
      .then((res) => setAllowAdd(!res))
      .catch(() => setAllowAdd(true));
  };

  useEffect(() => {
    fetchData();
    getAllMedicines()
      .then((res) => {
        setMedicine(res);
        setMedicineMap(res.reduce((acc: any, item: any) => { acc[item.id] = item; return acc; }, {}));
      })
      .catch((err) => console.error("Error fetching medicines:", err));
  }, [appointment?.patientId, appointment?.id]);

  const handleMedicine = (medicines:any[]) => {
    open();
    setMedicineData(medicines);
  };

  const insertMedicine = () => {
    form.insertListItem("medicines", { name: "", dosage: "", frequency: "", route: "", type: "", instructions: "" });
  };

  const removeMedicine = (index: number) => {
    form.removeListItem("medicines", index);
  };

  const handleChangeMed = (medId: any, index: number) => {
    if (medId && medId !== "OTHER") {
      form.setFieldValue(`medicines.${index}.medicineId`, medId);
      form.setFieldValue(`medicines.${index}.name`, medicineMap[medId]?.name || "");
      form.setFieldValue(`medicines.${index}.dosage`, medicineMap[medId]?.dosage || "");
      form.setFieldValue(`medicines.${index}.type`, medicineMap[medId]?.type || "");
    } else {
      form.setFieldValue(`medicines.${index}.medicineId`, "OTHER");
      form.setFieldValue(`medicines.${index}.name`, null);
      form.setFieldValue(`medicines.${index}.dosage`, null);
      form.setFieldValue(`medicines.${index}.type`, null);
    }
  };

  const renderSelectOption: SelectProps["renderOption"] = ({ option, checked }: any) => (
    <div className="flex gap-5 items-center flex-1">
      {option.label}
      {option?.manufacturer && <span style={{ fontStyle: "italic", color: "gray" }}>{option.manufacturer} - {option.dosage}</span>}
      {checked && <IconCheck style={{ marginInlineStart: "auto" }} />}
    </div>
  );

  const handleSubmit = (values: typeof form.values) => {
    if (values.medicines.length === 0) {
      errorNotification("Please add at least one medicine.");
      return;
    }
    const payload = {
      patientId: appointment.patientId,
      doctorId: appointment.doctorId,
      appointmentId: appointment.id,
      diagnosis: "",
      notes: values.notes || "",
      prescription: {
        doctorId: appointment.doctorId,
        patientId: appointment.patientId,
        appointmentId: appointment.id,
        notes: values.notes || "",
        medicines: values.medicines.map((med: any) => ({
          ...med,
          medicineId: med.medicineId === "OTHER" ? null : med.medicineId,
        })),
      },
    };
    setLoading(true);
    createAppointmentReport(payload)
      .then(() => {
        successNotification("Prescription added successfully!");
        form.reset();
        closeAdd();
        setAllowAdd(false);
        fetchData();
      })
      .catch((error) => {
        errorNotification(error?.response?.data?.errorMessage || "Error adding prescription");
      })
      .finally(() => setLoading(false));
  };

  const leftToolbarTemplate = () => (
    allowAdd ? (
      <Button leftSection={<IconPlus size={16} />} variant="filled" onClick={openAdd}>
        Add Prescription
      </Button>
    ) : null
  );

  const rightToolbarTemplate = () => {
     return <div className="md:flex hidden gap-5 items-center">
           <SegmentedControl
      value={view}
      size={matches? "sm":"md"}
      color="primary"
      onChange={setView}
      data={[
        { label: <IconTable />, value: 'table' },
        { label: <IconLayoutGrid />, value: 'card' }
      ]}
    />
         <TextInput className="lg:block hidden"
          leftSection={<IconSearch />}
          fw={400}
          value={globalFilterValue}
          onChange={onGlobalFilterChange}
          placeholder="Keyword Search"
        /></div>
    };

  return (
    <div>
      <Toolbar className="mb-4 !p-1" start={leftToolbarTemplate} end={rightToolbarTemplate} />

      {view === "table" && !matches
        ? <DataTable
            stripedRows
            size="small"
            value={data}
            paginator
            rows={10}
            paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
            rowsPerPageOptions={[10, 25, 50]}
            dataKey="id"
            filterDisplay="menu"
            globalFilterFields={["patientName", "notes"]}
            emptyMessage="No Appointment found."
            currentPageReportTemplate="Showing {first} to {last} of {totalRecords} entries"
          >
            <Column field="doctorName" header="Doctor" />
            <Column field="prescriptionDate" header="Prescription Date" sortable style={{ minWidth: "14rem" }} body={(rowData) => <>{formatDate(rowData.prescriptionDate)}</>} />
            <Column field="doctorName" header="Medicines" body={(rowData) => rowData.medicines?.length ?? 0} />
            <Column field="notes" header="Notes" style={{ minWidth: "14rem" }} />
            <Column headerStyle={{ width: "5rem", textAlign: "center" }} bodyStyle={{ textAlign: "center", overflow: "visible" }} body={actionBodyTemplate} />
          </DataTable>
        : <div className="grid lg:grid-cols-4 md:grid-cols-3 sm:grid-cols-2 grid-cols-1 gap-5">
            {data?.map((app) => <PresCard key={app.id} handleMedicine={handleMedicine} {...app} />)}
            {data?.length === 0 && <div className="col-span-4 text-center text-gray-500">No Prescription found.</div>}
          </div>
      }

      {/* View Medicines Modal */}
      <Modal opened={opened} size="xl" onClose={close} title="Medicines" centered>
        <div className="grid md:grid-cols-2 grid-cols-1 gap-5">
          {medicineData?.map((data: any, index: number) => (
            <Card key={index} shadow="md" radius="md" withBorder padding="lg">
              <Title order={4} mb="sm">{data.name} {data.type}</Title>
              <Divider my="sm" />
              <Grid>
                <Grid.Col span={6}><Text size="sm" fw={500}>Dosage:</Text><Text>{data.dosage}</Text></Grid.Col>
                <Grid.Col span={6}><Text size="sm" fw={500}>Frequency:</Text><Text>{data.frequency}</Text></Grid.Col>
                <Grid.Col span={6}><Text size="sm" fw={500}>Duration:</Text><Text>{data.duration}</Text></Grid.Col>
                <Grid.Col span={6}><Text size="sm" fw={500}>Route:</Text><Text>{data.route}</Text></Grid.Col>
                <Grid.Col span={6}><Text size="sm" fw={500}>Prescription ID:</Text><Text>{data.prescriptionId}</Text></Grid.Col>
                <Grid.Col span={6}><Text size="sm" fw={500}>Medicine ID:</Text><Text>{data.medicineId}</Text></Grid.Col>
                <Grid.Col span={6}><Text size="sm" fw={500}>Instructions:</Text><Text>{data.instructions}</Text></Grid.Col>
              </Grid>
            </Card>
          ))}
        </div>
        {medicineData.length === 0 && <Text c="dimmed" size="sm" mt="md">No medicines Prescribed for this appointment.</Text>}
      </Modal>

      {/* Add Prescription Modal */}
      <Modal
        opened={addOpened}
        onClose={closeAdd}
        size="xl"
        title={<span className="text-xl font-semibold text-primary-500">Add Prescription</span>}
        centered
      >
        <form onSubmit={form.onSubmit(handleSubmit)} className="flex flex-col gap-4">
          <TextInput
            label="Prescription Notes"
            placeholder="e.g. Take medicines after food"
            {...form.getInputProps("notes")}
          />

          <Fieldset legend={<span className="text-base font-medium text-primary-500">Medicines</span>} radius="md">
            <div className="flex flex-col gap-4">
              {form.values.medicines.map((med: Medicine, index: number) => (
                <Fieldset
                  key={index}
                  legend={
                    <div className="flex gap-3 items-center">
                      <span className="text-base font-medium">Medicine {index + 1}</span>
                      <ActionIcon onClick={() => removeMedicine(index)} variant="filled" color="red" size="sm">
                        <IconTrash size={14} />
                      </ActionIcon>
                    </div>
                  }
                  radius="md"
                >
                  <div className="grid sm:grid-cols-2 grid-cols-1 gap-4">
                    <Select
                      renderOption={renderSelectOption}
                      label="Medicine"
                      placeholder="Select Medicine"
                      withAsterisk
                      {...form.getInputProps(`medicines.${index}.medicineId`)}
                      onChange={(value: any) => handleChangeMed(value, index)}
                      data={[
                        ...medicine
                          .filter((x: any) => !form.values.medicines.some((item: any, idx: number) => item.medicineId === x.id && idx !== index))
                          .map((item: any) => ({ ...item, value: "" + item.id, label: item.name })),
                        { label: "Other", value: "OTHER" },
                      ]}
                    />
                    {med.medicineId === "OTHER" && (
                      <TextInput label="Medicine Name" placeholder="Enter medicine name" withAsterisk {...form.getInputProps(`medicines.${index}.name`)} />
                    )}
                    <TextInput label="Dosage" placeholder="e.g. 500mg" withAsterisk disabled={med.medicineId !== "OTHER" && !!med.medicineId} {...form.getInputProps(`medicines.${index}.dosage`)} />
                    <Select label="Frequency" placeholder="Pick frequency" withAsterisk data={dosageFrequencies} {...form.getInputProps(`medicines.${index}.frequency`)} />
                    <NumberInput label="Duration (days)" placeholder="Enter duration" withAsterisk {...form.getInputProps(`medicines.${index}.duration`)} />
                    <Select label="Type" placeholder="Select type" withAsterisk data={medicineTypes} {...form.getInputProps(`medicines.${index}.type`)} />
                    <TextInput label="Instructions" placeholder="e.g. After meals" withAsterisk {...form.getInputProps(`medicines.${index}.instructions`)} />
                  </div>
                </Fieldset>
              ))}
              <Button onClick={insertMedicine} variant="outline" color="blue">
                + Add Medicine
              </Button>
            </div>
          </Fieldset>

          <div className="flex gap-4">
            <Button type="submit" loading={loading} variant="filled" color="primary" fullWidth>
              Submit Prescription
            </Button>
            <Button onClick={closeAdd} variant="filled" color="red" fullWidth>
              Cancel
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default Prescription;
