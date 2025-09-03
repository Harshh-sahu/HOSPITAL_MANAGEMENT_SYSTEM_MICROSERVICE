import {
  ActionIcon,
  Button,
  Fieldset,
  MultiSelect,
  NumberInput,
  Select,
  Textarea,
  TextInput,
} from "@mantine/core";
import React, { useEffect, useState } from "react";
import {
  dosageFrequencies,
  medicineTypes,
  routess,
  symptoms,
  tests,
} from "../../../Data/DropDownData";
import { IconEye, IconSearchOff, IconTrash } from "@tabler/icons-react";
import { useForm } from "@mantine/form";
import { createAppointmentReport, getReportByPatientId, isReportExists } from "../../../Service/AppointmentService";
import {
  errorNotification,
  successNotification,
} from "../../../Utility/NotificationUtil";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { FilterMatchMode } from "primereact/api";
import { DataTable, DataTableFilterMeta } from "primereact/datatable";
import { Column } from "primereact/column";
import { formatDate } from "../../../Utility/DateUtility";
import { render } from "@testing-library/react";

const ApReport = ({ appointment }: any) => {
  const [loading, setLoading] = React.useState(false);
  const dispatch = useDispatch();
  type Medicine = {
    name: string;
    medicineId?: number;
    dosage: string;
    frequency: string;
    route: string;
    type: string;
    instructions: string;
    prescriptionId?: number;
  };
  const form = useForm<any>({
    initialValues: {
      symptoms: [],
      tests: [],
      diagnosis: "",
      referral: "",
      notes: "",
      prescription: {
        medicines: [] as Medicine[],
      },
    },
    validate: {
      symptoms: (value) =>
        value.length > 0 ? null : "Please Select at least one ",
      diagnosis: (value) => (value.length > 0 ? null : "Diagnosis is required"),
      prescription: {
        medicines: {
          name: (value) => (value?.trim() ? null : "Medicine name is required"),
          dosage: (value) => (value?.trim() ? null : "Dosage is required"),
          frequency: (value) =>
            value?.trim() ? null : "Frequency is required",
          route: (value) => (value?.trim() ? null : "Route is required"),
          type: (value) => (value?.trim() ? null : "Type is required"),
          instructions: (value) =>
            value?.trim() ? null : "Instructions are required",
        },
      },
    },
  });
  const insertMedicine = () => {
    form.insertListItem("prescription.medicines", {
      name: "",
      dosage: "",
      frequency: "",
      route: "",
      type: "",
      instructions: "",
    });
  };
  const removeMedicine = (index: number) => {
    form.removeListItem("prescription.medicines", index);
  };

  useEffect(()=>{
   fetchData();
   
  },[appointment?.patientId, appointment?.id]);

  const fetchData =()=>{
     if(!appointment?.patientId)return;
    getReportByPatientId(appointment?.patientId).then((res)=>{
console.log("Fetched report data:", res);
setData(res);
    }).catch((error)=>{
console.error("Error fetching report data:", error);
    });
     isReportExists(appointment?.id).then((res)=>{
      setAllowAdd(!res);
      console.log("Report existence check:", res);
    }).catch((error)=>{
      console.error("Error checking report existence:", error);
      setAllowAdd(true);
    
    });

  }
  const handleSubmit = (values: typeof form.values) => {
    console.log("form submitted with value", values);
    let data = {
      ...values,
      doctorId: appointment.doctorId,
      patientId: appointment.patientId,
      appointmentId: appointment.id,
      prescription: {
        ...values.prescription,
        doctorId: appointment.doctorId,
        patientId: appointment.patientId,
        appointmentId: appointment.id,
      },
    };

    setLoading(true);
    createAppointmentReport(data)
      .then((res) => {
        successNotification("Report created successfully");
        console.log("Report created successfully:", res);
        form.reset();
        setEdit(false);
        setAllowAdd(false);
        fetchData();
      })
      .catch((error) => {
        errorNotification(
          error?.response?.data?.errorMessage || "Error creating report"
        );
        console.error("Error creating report:", error);
      })
      .finally(() => {
        setLoading(false);
      });
  };
  const [filters, setFilters] = useState<DataTableFilterMeta>({
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
  });
  const onGlobalFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    let _filters: any = { ...filters };

    _filters["global"].value = value;

    setFilters(_filters);
    setGlobalFilterValue(value);
  };
  const [data,setData] = useState<any[]>([]);
  const[allowAdd,setAllowAdd] = useState<boolean>(false);
  const [edit,setEdit] = useState<boolean>(false);
  const navigate = useNavigate();
  const [globalFilterValue, setGlobalFilterValue] = useState<string>("");
  const renderHeader = () => {
    return (
      <div className="flex flex-wrap gap-2 justify-between tems-center">
        {allowAdd && <Button onClick={()=>setEdit(true)} variant="filled">Add Report</Button>}
        <TextInput
          leftSection={<IconSearchOff />}
          fw={400}
          value={globalFilterValue}
          onChange={onGlobalFilterChange}
          placeholder="Keyword Search"
        />
      </div>
    );
  };
  const header = renderHeader();
  const actionBodyTemplate = (rowData: any) => {
    return (
      <div className="flex gap-2">
        <ActionIcon
          onClick={() =>
            navigate("/doctor/appointment/" + rowData.appointmentId)
          }
        >
          <IconEye size={20} stroke={1.5} />
        </ActionIcon>
        
      </div>
    );
  };

  return (
    <div>
     { !edit? <DataTable
        header={header}
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

        <Column
          field="diagnosis"
          header="Diagnosis"
          body={(rowData) => rowData.diagnosis ?? ""}
        />
        <Column
          field="reportDate"
          header="Report Date"
          sortable
          style={{ minWidth: "14rem" }}
          body={(rowData) => <>{formatDate(rowData.createdAt)}</>}
        />

  

        <Column field="notes" header="Notes"  />
        {/* <Column
          headerStyle={{ width: "5rem", textAlign: "center" }}
          bodyStyle={{ textAlign: "center", overflow: "visible" }}
          body={actionBodyTemplate}
        /> */}
        {/* <Column
                     headerStyle={{ width: "5rem", textAlign: "center" }}
                     bodyStyle={{ textAlign: "center", overflow: "visible" }}
                     body={actionBodyTemplate}
                   /> */}
      </DataTable>

      :<form onSubmit={form.onSubmit(handleSubmit)}>
        <Fieldset
          className="grid gap-5 grid-cols-2"
          legend={
            <span className="text-lg font-medium text-primary-500">
              Personal information
            </span>
          }
          radius="md"
        >
          <MultiSelect
            {...form.getInputProps("symptoms")}
            className="col-span-2"
            label="Symptoms"
            placeholder="Pick symtoms"
            data={symptoms}
          />
          <MultiSelect
            {...form.getInputProps("tests")}
            className="col-span-2"
            withAsterisk
            label="Tests"
            placeholder="Pick tests"
            data={tests}
          />
          <TextInput
            {...form.getInputProps("diagnosis")}
            label="Diagnosis"
            placeholder="Enter Diagnosis"
            withAsterisk
          />
          <TextInput
            {...form.getInputProps("referral")}
            label="Referral"
            placeholder="Enter Referral details"
          />
          <Textarea
            {...form.getInputProps("notes")}
            className="col-span-2"
            label="Notes"
            placeholder="Enter any additional notes"
          />
        </Fieldset>
        <Fieldset
          className="grid gap-5 "
          legend={
            <span className="text-lg font-medium text-primary-500">
              Prescription
            </span>
          }
          radius="md"
        >
          {form.values.prescription.medicines.map(
            (_medicine: Medicine, index: number) => (
              <Fieldset
                legend={
                  <div className=" flex gap-5 items-center">
                    <h1 className="text-lg font-medium">
                      Medicine {index + 1}
                    </h1>
                    <ActionIcon
                      onClick={() => removeMedicine(index)}
                      variant="filled"
                      color="red"
                      size="md"
                      className="mb-2"
                    >
                      <IconTrash />
                    </ActionIcon>
                  </div>
                }
                className="grid gap-5 col-span-2 grid-cols-2"
              >
                <TextInput
                  {...form.getInputProps(
                    `prescription.medicines.${index}.name`
                  )}
                  label="Medicine"
                  placeholder="Enter medicineName"
                  withAsterisk
                />
                <TextInput
                  {...form.getInputProps(
                    `prescription.medicines.${index}.dosage`
                  )}
                  label="Dosage"
                  placeholder="Enter dosage "
                  withAsterisk
                />

                <Select
                  {...form.getInputProps(
                    `prescription.medicines.${index}.frequency`
                  )}
                  label="Frequency"
                  placeholder="Pick frequency"
                  data={dosageFrequencies}
                  withAsterisk
                />
                <NumberInput
                  {...form.getInputProps(
                    `prescription.medicines.${index}.duration`
                  )}
                  label="Duration (in days)"
                  placeholder="Enter duration"
                  withAsterisk
                />

                <Select
                  label="Route"
                  {...form.getInputProps(
                    `prescription.medicines.${index}.route`
                  )}
                  placeholder="Pick route"
                  data={routess}
                  withAsterisk
                />
                <Select
                  label="Type"
                  {...form.getInputProps(
                    `prescription.medicines.${index}.type`
                  )}
                  placeholder="Select type"
                  data={medicineTypes}
                  withAsterisk
                />
                <TextInput
                  label="Instructions"
                  {...form.getInputProps(
                    `prescription.medicines.${index}.instructions`
                  )}
                  placeholder="Enter instructions"
                  withAsterisk
                />
              </Fieldset>
            )
          )}
          <div className="flex items-center justify-center col-span-2">
            <Button
              onClick={insertMedicine}
              variant="outline"
              color="blue"
              className=" col-span-2 mt-4"
            >
              Add Medicine
            </Button>
          </div>
        </Fieldset>
        <div className="flex items-center gap-5 justify-center mt-4">
          <Button
            loading={loading}
            type="submit"
            className="w-full"
            variant="filled"
            color="primary"
          >
            Submit Report
          </Button>
          <Button
            loading={loading}
            className="w-full"
            variant="filled"
            color="red"
          >
            Cancel
          </Button>
        </div>
      </form>
}</div>
  );
};

export default ApReport;
