import {
  ActionIcon,
  Button,
  Fieldset,
  NumberInput,
  SegmentedControl,
  Select,
  TextInput,
} from "@mantine/core";
import React, { useEffect, useState } from "react";
import {
  medicineCategories,
  medicineType,
} from "../../../Data/DropDownData";
import { IconEdit, IconLayoutGrid, IconSearch, IconSearchOff, IconTable } from "@tabler/icons-react";
import { useForm } from "@mantine/form";
import {
  errorNotification,
  successNotification,
} from "../../../Utility/NotificationUtil";
import { FilterMatchMode } from "primereact/api";
import { DataTable, DataTableFilterMeta } from "primereact/datatable";
import { Column } from "primereact/column";
import {
  addMedicine,
  getAllMedicines,
  updateMedicine,
} from "../../../Service/MedicineService";
import { capitalizeFirstLetter } from "../../../Utility/OtherUtility";
import { Toolbar } from "primereact/toolbar";
import MedCard from "./MedCard";
import ReportCard from "../../Doctor/Appointment/ReportCard";

const Medicine = ({ appointment }: any) => {
  const [loading, setLoading] = React.useState(false);

  const form = useForm<any>({
    initialValues: {
      name: "",
      dosage: "",
      category: "",
      type: "",
      manufacturer: "",
      unitPrice: "",
    },
    validate: {
      name: (value) => (value ? null : "Medicine name is required"),
      dosage: (value) => (value ? null : "Dosage is required"),
      category: (value) => (value ? null : "Category is required"),
      type: (value) => (value ? null : "Type is required"),
      manufacturer: (value) => (value ? null : "Manufacturer is required"),
      unitPrice: (value) => (value ? null : "Unit Price is required"),
    },
  });

  const [data, setData] = useState<any[]>([]);
  const [edit, setEdit] = useState<boolean>(false);
  const [filters, setFilters] = useState<DataTableFilterMeta>({
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
  });
  const [globalFilterValue, setGlobalFilterValue] = useState<string>("");

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = () => {
    getAllMedicines()
      .then((res) => {
        console.log("Fetched medicine data:", res);
        setData(res);
      })
      .catch((error) => {
        console.error("Error fetching medicine data:", error);
      });
  };
const handleSubmit = (values: any) => {
  console.log("form submitted with value", values);

  setLoading(true);

  if (values.id) {
    updateMedicine(values.id, values)  // 👈 id aur values dono bhejo
      .then((_res) => {
        successNotification("Medicine updated successfully");
        fetchData();
        form.reset();
        setEdit(false);
      })
      .catch((error) => {
        console.error("Error updating medicine:", error);
        errorNotification(`Error updating medicine: ${error.message}`);
      })
      .finally(() => setLoading(false));
  } else {
    addMedicine(values)
      .then((_res) => {
        successNotification("Medicine added successfully");
        fetchData();
        form.reset();
        setEdit(false);
      })
      .catch((error) => {
        console.error("Error adding medicine:", error);
        errorNotification(`Error adding medicine: ${error.message}`);
      })
      .finally(() => setLoading(false));
  }
};

  const onGlobalFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    let _filters: any = { ...filters };

    _filters["global"].value = value;
    setFilters(_filters);
    setGlobalFilterValue(value);
  };

  const [view,setView]=useState<string>("table");
  const renderHeader = () => {
    return (
      <div className="flex flex-wrap gap-2 justify-between items-center">
        <Button
          onClick={() => {
            form.reset(); // 👈 ensure add mode clears old id
            setEdit(true);
          }}
          variant="filled"
        >
          Add Medicine
        </Button>
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

    const rightToolbarTemplate = () => {
       return <div className="flex gap-5 items-center">
  
             <SegmentedControl
        value={view}
        color="primary"
        onChange={setView}
        data={[
          { label: <IconTable />, value: 'table' },
          { label: <IconLayoutGrid />, value: 'card' }
        
        ]}
      />
  
           <TextInput
            leftSection={<IconSearch
               />}
            fw={400}
            value={globalFilterValue}
            onChange={onGlobalFilterChange}
            placeholder="Keyword Search"
          /></div>
      };
  const onEdit = (rowData: any) => {
    setEdit(true);
    console.log("rowData:", rowData);
    form.setValues({
      ...rowData,
      name: rowData?.name,
      dosage: rowData?.dosage,
      category: rowData?.category,
      type: rowData?.type,
      manufacturer: rowData?.manufacturer,
      unitPrice: rowData?.unitPrice,
    });
  };


  return (
    <div>
      {!edit ? (
        <div>
        
               <Toolbar
                       className="mb-4 !p-1"
                  
                       end={rightToolbarTemplate}></Toolbar>
         
               {view==="table"?
        <DataTable
          stripedRows
          size="small"
          value={data}
          paginator
          rows={10}
          paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
          rowsPerPageOptions={[10, 25, 50]}
          dataKey="medicineId"
          filterDisplay="menu"
          globalFilterFields={["name", "manufacturer", "category", "type"]}
          emptyMessage="No Medicine found."
          currentPageReportTemplate="Showing {first} to {last} of {totalRecords} entries"
        >
          <Column field="name" header="Name" />
          <Column field="dosage" header="Dosage" />
          <Column field="stock" header="Stock" />
          <Column
            field="category"
            header="Category"
            body={(rowData) => capitalizeFirstLetter(rowData.category) ?? ""}
          />
          <Column
            field="type"
            header="Type"
            body={(rowData) => capitalizeFirstLetter(rowData.type) ?? ""}
          />
          <Column
            field="manufacturer"
            header="Manufacturer"
            body={(rowData) =>
              capitalizeFirstLetter(rowData.manufacturer) ?? ""
            }
          />
          <Column
            field="unitPrice"
            header="Unit Price (₹)"
            sortable
            body={(rowData) => rowData.unitPrice ?? ""}
          />
          <Column
            headerStyle={{ width: "5rem", textAlign: "center" }}
            bodyStyle={{ textAlign: "center", overflow: "visible" }}
        
          />
        </DataTable>
        :         <div className="grid grid-cols-4 gap-5">
                {
        
                  data?.map((app)=> <MedCard key={app.id} {...app}  />)
                }
                {
                  data?.length===0 && <div className="col-span-4 text-center text-gray-500">No Medicine found.</div>
                }
              </div>}
                </div>
        
      ) : (
        <form onSubmit={form.onSubmit(handleSubmit)}>
          <Fieldset
            className="grid gap-5 grid-cols-2"
            legend={
              <span className="text-lg font-medium text-primary-500">
                Medicine information
              </span>
            }
            radius="md"
          >
            <TextInput
              {...form.getInputProps("name")}
              label="Medicine"
              placeholder="Enter Name"
              withAsterisk
            />
            <TextInput
              {...form.getInputProps("dosage")}
              label="Dosage"
              placeholder="Enter Dosage details"
            />
            <Select
              {...form.getInputProps("category")}
              label="Category"
              placeholder="Select Category"
              data={medicineCategories}
            />
            <Select
              {...form.getInputProps("type")}
              label="Type"
              placeholder="Select Type"
              data={medicineType}
            />
            <TextInput
              {...form.getInputProps("manufacturer")}
              label="Manufacturer"
              placeholder="Enter Manufacturer details"
            />
            <NumberInput
              {...form.getInputProps("unitPrice")}
              label="Price"
              placeholder="Enter Price"
              min={0}
              clampBehavior="strict"
            />
          </Fieldset>

          <div className="flex items-center gap-5 justify-center mt-4">
            <Button
              loading={loading}
              type="submit"
              className="w-full"
              variant="filled"
              color="primary"
            >
              {form.values.id ? "Update Medicine" : "Add Medicine"}
            </Button>
            <Button
              loading={loading}
              className="w-full"
              variant="filled"
              color="red"
              onClick={() => {
                setEdit(false);
              }}
            >
              Cancel
            </Button>
          </div>
        </form>
      )}
    </div>
  );
};

export default Medicine;
