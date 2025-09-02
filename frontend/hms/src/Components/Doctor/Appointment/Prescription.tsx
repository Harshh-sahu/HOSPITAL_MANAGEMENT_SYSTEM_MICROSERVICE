import { ActionIcon, Button, TextInput } from "@mantine/core";
import { IconEye, IconSearchOff, IconTrash } from "@tabler/icons-react";
import { FilterMatchMode, FilterOperator } from "primereact/api";
import { Column } from "primereact/column";
import { DataTable, DataTableFilterMeta } from "primereact/datatable";
import React, { useEffect, useState } from "react";
import { getPrescriptionByPatientId } from "../../../Service/AppointmentService";
import { formatDate } from "../../../Utility/DateUtility";
import { Tag } from "primereact/tag";
import { useNavigate } from "react-router-dom";

const Prescription = ({ appointment }: any) => {
  const [data, setData] = useState<any[]>([]);
  const [filters, setFilters] = useState<DataTableFilterMeta>({
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
  });
  const actionBodyTemplate = (rowData:any) => {
    return <div className="flex gap-2">
        <ActionIcon onClick={()=> navigate("/doctor/appointment/"+rowData.appointmentId)}>
            <IconEye size={20} stroke={1.5} />
        </ActionIcon>
        
    </div>
  };

  const onGlobalFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    let _filters: any = { ...filters };

    _filters["global"].value = value;

    setFilters(_filters);
    setGlobalFilterValue(value);
  };
    const navigate = useNavigate();

  const [globalFilterValue, setGlobalFilterValue] = useState<string>("");
   console.log("patientId:", appointment?.patientId);
  useEffect(() => {


    getPrescriptionByPatientId(appointment?.patientId)
    
      .then((res) => {
        console.log("Fetched Prescription data:", res);
        setData(res);
      })
      .catch((err) => {
        console.error("Error fetching Prescription data:", err);
      });
  },[appointment?.patientId]);
  const renderHeader = () => {
    return (
      <div className="flex flex-wrap gap-2 justify-end tems-center">
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

  return (
    <div>
      <DataTable
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
        <Column field="doctorName" header="Doctor"  />

        <Column
          field="prescriptionDate"
          header="Prescription Date"
          sortable
          style={{ minWidth: "14rem" }}
          body={(rowData) => <>{formatDate(rowData.prescriptionDate)}</>}
        />

   <Column field="doctorName" header="Medicines"  body={(rowData)=>rowData.medicines?.length??0} />

    
        <Column
          field="notes"
          header="Notes"
        
          style={{ minWidth: "14rem" }}
        />
        <Column
          headerStyle={{ width: "5rem", textAlign: "center" }}
          bodyStyle={{ textAlign: "center", overflow: "visible" }}
          body={actionBodyTemplate}
        />
        {/* <Column
               headerStyle={{ width: "5rem", textAlign: "center" }}
               bodyStyle={{ textAlign: "center", overflow: "visible" }}
               body={actionBodyTemplate}
             /> */}
      </DataTable>
    </div>
  );
};

export default Prescription;
